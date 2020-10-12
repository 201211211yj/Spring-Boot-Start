package tacos.web;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@Component
@ConfigurationProperties(prefix="taco.orders")
@Data
@Validated
public class OrderProps {
	@Min(value = 5, message = "5와 25 사이의 숫자 입력해야함")
	@Max(value = 25, message = "5와 25 사이의 숫자 입력해야함")
	private int pageSize = 20;
}
