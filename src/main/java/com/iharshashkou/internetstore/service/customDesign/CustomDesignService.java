package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.DTO.customDesign.*;
import com.iharshashkou.internetstore.entity.Category;
import com.iharshashkou.internetstore.entity.User;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesign;
import com.iharshashkou.internetstore.entity.customDesignEntity.CustomDesignStatus;
import com.iharshashkou.internetstore.exception.CustomDesignValidationException;
import com.iharshashkou.internetstore.repository.CategoryRepository;
import com.iharshashkou.internetstore.repository.CustomDesignRepository;
import com.iharshashkou.internetstore.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import java.math.BigDecimal;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomDesignService {

    private final CustomDesignRepository customDesignRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CustomDesignPricingService pricingService;
    private final CustomDesignValidationService validationService;
    private final SecureRandom secureRandom = new SecureRandom();

    public CreateCustomDesignResponse createDesign(
            Long userId,
            CreateCustomDesignRequest request
    ) {
        // Проверяем параметры проекта.
        validationService.validateDimensions(request);

        // Проверяем существование и активность категории.
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Категория не найдена")
                );

        if (!category.isActive()) {
            throw new CustomDesignValidationException(
                    "Выбранная категория недоступна"
            );
        }

        // Если пользователь авторизован, привязываем проект к аккаунту.
        User user = null;

        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new EntityNotFoundException("Пользователь не найден")
                    );
        }

        // Рассчитываем предварительную стоимость.
        BigDecimal estimatedPrice = pricingService.calculatePrice(request);

        CustomDesign design = CustomDesign.builder()
                .user(user)
                .category(category)
                .woodType(request.woodType())
                .shape(request.shape())
                .epoxyEnabled(request.epoxyEnabled())
                .epoxyColor(request.epoxyColor())
                .background(request.background())
                .lengthCm(request.lengthCm())
                .widthCm(request.widthCm())
                .heightCm(request.heightCm())
                .diameterCm(request.diameterCm())
                .comment(request.comment())
                .estimatedPrice(estimatedPrice)
                .status(CustomDesignStatus.GENERATED)
                .build();

        String guestAccessToken = null;

        if (user == null) {
            guestAccessToken = generateGuestToken();
            design.setGuestTokenHash(hashGuestToken(guestAccessToken));
        }

        design = customDesignRepository.save(design);

        return new CreateCustomDesignResponse(
                getDesignResponse(design),
                guestAccessToken
        );
    }

    public CustomDesignResponse getGuestDesign(
            Long designId,
            String guestAccessToken
    ) {

        if (guestAccessToken == null || guestAccessToken.isBlank()) {
            throw new CustomDesignValidationException(
                    "Необходимо указать токен доступа"
            );
        }

        String tokenHash = hashGuestToken(guestAccessToken);

        CustomDesign design = customDesignRepository
                .findByIdAndGuestTokenHash(
                        designId,
                        tokenHash
                )
                .orElseThrow(() ->
                        new EntityNotFoundException("Проект не найден")
                );

        return getDesignResponse(design);
    }

    public List<CustomDesignResponse> getUserDesigns(Long userId) {

        return customDesignRepository
                .findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::getDesignResponse)
                .toList();
    }

    public CustomDesignResponse getUserDesign(
            Long userId,
            Long designId
    ) {
        CustomDesign design = customDesignRepository
                .findByIdAndUser_Id(designId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Проект не найден")
                );

        return getDesignResponse(design);
    }

    public List<CustomDesignAdminResponse> getAllDesigns() {

        return customDesignRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::getAdminDesignResponse)
                .toList();
    }

    public CustomDesignAdminResponse reviewDesign(
            Long designId,
            ReviewCustomDesignRequest request
    ) {
        CustomDesign design = customDesignRepository.findById(designId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Проект не найден")
                );

        if (design.getStatus() != CustomDesignStatus.SUBMITTED) {
            throw new CustomDesignValidationException(
                    "Рассматривать можно только отправленные проекты"
            );
        }

        if (request.status() != CustomDesignStatus.APPROVED
                && request.status() != CustomDesignStatus.REJECTED) {
            throw new CustomDesignValidationException(
                    "Допустимые статусы: APPROVED или REJECTED"
            );
        }

        if (request.status() == CustomDesignStatus.APPROVED
                && request.finalPrice() == null) {
            throw new CustomDesignValidationException(
                    "Для одобрения проекта необходимо указать окончательную стоимость"
            );
        }

        if (request.status() == CustomDesignStatus.REJECTED
                && request.finalPrice() != null) {
            throw new CustomDesignValidationException(
                    "Для отклонённого проекта нельзя указывать окончательную стоимость"
            );
        }

        design.setStatus(request.status());
        design.setFinalPrice(request.finalPrice());
        design.setAdminComment(request.adminComment());

        design = customDesignRepository.save(design);

        return getAdminDesignResponse(design);
    }

    public CustomDesignResponse submitDesign(
            Long designId,
            Long userId,
            String guestAccessToken
    ) {
        CustomDesign design;

        if (userId != null) {
            design = customDesignRepository
                    .findByIdAndUser_Id(designId, userId)
                    .orElseThrow(() ->
                            new EntityNotFoundException("Проект не найден")
                    );
        } else {
            if (guestAccessToken == null || guestAccessToken.isBlank()) {
                throw new CustomDesignValidationException(
                        "Необходимо указать токен доступа"
                );
            }

            String tokenHash = hashGuestToken(guestAccessToken);

            design = customDesignRepository
                    .findByIdAndGuestTokenHash(designId, tokenHash)
                    .orElseThrow(() ->
                            new EntityNotFoundException("Проект не найден")
                    );
        }

        if (design.getStatus() != CustomDesignStatus.GENERATED) {
            throw new CustomDesignValidationException(
                    "На рассмотрение можно отправить только созданный проект"
            );
        }

        design.setStatus(CustomDesignStatus.SUBMITTED);

        design = customDesignRepository.save(design);

        return getDesignResponse(design);
    }

    private CustomDesignResponse getDesignResponse(CustomDesign design) {
        return new CustomDesignResponse(
                design.getId(),
                design.getCategory().getId(),
                design.getCategory().getName(),
                design.getWoodType(),
                design.getShape(),
                design.isEpoxyEnabled(),
                design.getEpoxyColor(),
                design.getBackground(),
                design.getLengthCm(),
                design.getWidthCm(),
                design.getHeightCm(),
                design.getDiameterCm(),
                design.getComment(),
                design.getImageUrl(),
                design.getEstimatedPrice(),
                design.getFinalPrice(),
                design.getStatus(),
                design.getCreatedAt()
        );
    }

    private String generateGuestToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);
    }

    private String hashGuestToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(
                    "Не удалось создать хеш токена", ex
            );
        }
    }

    private CustomDesignAdminResponse getAdminDesignResponse(
            CustomDesign design
    ) {
        User user = design.getUser();

        return new CustomDesignAdminResponse(
                design.getId(),
                user != null ? user.getId() : null,
                user != null ? user.getName() : null,
                user != null ? user.getEmail() : null,
                design.getCategory().getId(),
                design.getCategory().getName(),
                design.getWoodType(),
                design.getShape(),
                design.isEpoxyEnabled(),
                design.getEpoxyColor(),
                design.getBackground(),
                design.getLengthCm(),
                design.getWidthCm(),
                design.getHeightCm(),
                design.getDiameterCm(),
                design.getComment(),
                design.getAdminComment(),
                design.getImageUrl(),
                design.getEstimatedPrice(),
                design.getFinalPrice(),
                design.getStatus(),
                design.getCreatedAt()
        );
    }
}
