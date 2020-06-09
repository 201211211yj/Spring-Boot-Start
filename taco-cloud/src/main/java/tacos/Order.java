package tacos;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class Order {
	@NotBlank(message="Name is required")
	private String deliveryName;

	@NotBlank(message="Street is required")
	private String deliveryStreet;
}
