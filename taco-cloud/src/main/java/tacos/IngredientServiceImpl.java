package tacos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IngredientServiceImpl implements IngredientService{
	
	Connection connection = null;
	PreparedStatement statement = null;
	ResultSet resultSet = null;
	
	@Override
	public Ingredient findById(String id) {
		// TODO Auto-generated method stub
		try {
			Ingredient ingredient = null;
			
			connection = DriverManager.getConnection("URL");
			statement = connection.prepareStatement("select id, name, type from ingredient where id = ?");
			statement.setString(1, id);
			resultSet = statement.executeQuery();
			
			if(resultSet.next()) {
				ingredient = new Ingredient(
					resultSet.getString("id"),
					resultSet.getString("name"),
					Ingredient.Type.valueOf(resultSet.getString("type"))
				);
			}
			return ingredient;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// 여기서는 무엇을 해야하나
			e.printStackTrace();
		}finally {
			if(resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
