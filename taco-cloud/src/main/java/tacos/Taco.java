package tacos;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Taco {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)//데이터베이스가 자동으로 생성해주는 ID값 사용
	private Long id;
	private Date createdAt;
	
	@NotNull
	@Size(min=5, message="Name must be at least 5 characters long")
	private String name;
	
	@ManyToMany(targetEntity=Ingredient.class)//하나의 Taco객체는 많은 Ingredient객체 가짐, 반대도 같음
	@Size(min=1, message="You must choose at least 1 ingredient")
	private List<Ingredient> Ingredients;
	
	@PrePersist//Taco 객체 저장되기 전에 createdAt 속성을 현재 일자로 설정
	void createdAt() {
		this.createdAt = new Date();
	}
}
