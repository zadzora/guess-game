package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    private static final String URL = "jdbc:postgresql://localhost/gamestudio";

    private static final String LOGIN = "postgres";

    private static final String PASSWORD = "123";

    private static final String INSERT_COMMAND = "INSERT INTO rating (player, game, rating, ratedon) VALUES (?, ?, ?, ?)";

    private static final String SELECT_COMMAND = "SELECT player, game, rating, ratedon FROM rating WHERE game = ? ORDER BY ratedon DESC";

    private static final String SELECT_RATING = "SELECT rating FROM rating WHERE game = ?";

    @Override
    public void setRating(Rating rating){
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(INSERT_COMMAND);
        ) {
            stmt.setString(1, rating.getPlayer());
            stmt.setString(2, rating.getGame());
            stmt.setInt(3, rating.getRating());
            stmt.setTimestamp(4, new Timestamp(rating.getRatedon().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getAverageRating(String game) {
        int averageRating = 0;
        int divide=0;
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(SELECT_COMMAND);
        ) {

            stmt.setString(1, game);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    divide++;
                    averageRating += rs.getInt(3);
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(divide != 0) {
            return averageRating / divide;
        }else{
            return 0;
        }
    }

    @Override
    public int getRating(String game, String player) {

        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(SELECT_COMMAND);
        ) {

            stmt.setString(1, game);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rating rating = new Rating(
                            rs.getString(1), rs.getString(2),
                            rs.getInt(3), rs.getTimestamp(4));
                   return rs.getInt(3);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
     }


}