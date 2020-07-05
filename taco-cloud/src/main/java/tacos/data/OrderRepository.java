package tacos.data;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tacos.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {
	List <Order> findByDeliveryZip(String deliveryZip);
	
	List <Order> readOrdersByDeliveryZipAndPlacedAtBetween(String deliveryZip, Date startDate, Date endDate);
	//get||find||read 찾을클래스 By 조건클래스 And 조건클래스 Between
	//이런걸 DSL(Domain Specific Language라 함)
}
