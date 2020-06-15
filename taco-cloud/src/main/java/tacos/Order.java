package tacos;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class Order {
	
	private Long id;
	private Date placedAt;
	
	@NotBlank(message="required")
	private String deliveryName;

	@NotBlank(message="required")
	private String deliveryStreet;
	
	@NotBlank(message="required")
	private String deliveryCity;

	@NotBlank(message="required")
	private String deliveryState;
	
	@NotBlank(message="required")
	private String deliveryZip;

	@NotBlank(message="required")
	private String ccNumber;
	
	@NotBlank(message="required")
	private String ccExpired;
	
	@NotBlank(message="required")
	private String ccCVV;

}
