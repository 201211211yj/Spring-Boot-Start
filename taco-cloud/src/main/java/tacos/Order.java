package tacos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
@Entity
@Table(name="taco_Order")
public class Order implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
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
	private String ccExpiration;
	
	@NotBlank(message="required")
	private String ccCVV;
	
	@ManyToMany(targetEntity=Taco.class)
	private List<Taco> tacos = new ArrayList<>();
	
	public void addDesign(Taco taco) {
		tacos.add(taco);
	}
	
	@PrePersist
	void placedAt() {
		this.placedAt = new Date();
	}
}