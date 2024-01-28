package com.techchallenge.infrastructure.persistence.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document(collection = "payments")
public class PaymentDocument {

	@Id
	private String id;
	private String title;
	private String description;
	private String notificationUrl;
	private BigDecimal totalAmount;
	private String orderStatus;
	private List<ItemDocument> items;
	private LocalDateTime createTime;
	private Boolean sent;


}
