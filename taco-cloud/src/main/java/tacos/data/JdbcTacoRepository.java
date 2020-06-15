package tacos.data;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import tacos.Ingredient;
import tacos.Taco;

public class JdbcTacoRepository implements TacoRepository {

	private JdbcTemplate jdbc;
	
	public JdbcTacoRepository (JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public Taco save(Taco taco) {
		// TODO Auto-generated method stub
		long tacoId = saveTacoInfo(taco);
		taco.setId(tacoId);
		for(Ingredient ingredient : taco.getIngredients()) {
			saveIngredientToTaco(ingredient, tacoId);
		}
		return taco;
	}
	
	private long saveTacoInfo(Taco taco) {
		taco.setCreatedAt(new Date());
		PreparedStatementCreator psc = new PreparedStatementCreatorFactory(
				"insert into Taco (name, createdAt) values (?, ?)", Types.VARCHAR, Types.TIMESTAMP
				).newPreparedStatementCreator(
						Arrays.asList(taco.getName(), taco.getCreatedAt()
				)
		);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(psc,keyHolder);
		
		return keyHolder.getKey().longValue();
	}
	
	private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
		jdbc.update("insert into Taco_Ingredients (taco, ingredient) values(?,?)",
				tacoId, ingredient.getId());
	}
}
