package ru.yandex.practicum.commerce.interaction_api.payment.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.commerce.interaction_api.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentDto {

    private UUID paymentId;

    @PositiveOrZero
    private BigDecimal totalPayment;

    @PositiveOrZero
    private BigDecimal totalProduct;

    @PositiveOrZero
    private BigDecimal deliveryTotal;

    @PositiveOrZero
    private BigDecimal feeTotal;

    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
}