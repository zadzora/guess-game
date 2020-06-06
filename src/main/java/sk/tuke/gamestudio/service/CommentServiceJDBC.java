package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceJDBC implements CommentService {
    private static final String URL = "jdbc:postgresql://localhost/gamestudio";

    private static final String LOGIN = "postgres";

    private static final String PASSWORD = "123";

    private static final String INSERT_COMMAND = "INSERT INTO comment (player, game, comment, commentedon) VALUES (?, ?, ?, ?)";

    private static final String SELECT_COMMAND = "SELECT player, game, comment, commentedon FROM comment WHERE game = ? ORDER BY commentedon DESC";


    @Override
    public void addComment(Comment comment){
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(INSERT_COMMAND);
        ) {
            stmt.setString(1, comment.getPlayer());
            stmt.setString(2, comment.getGame());
            stmt.setString(3, comment.getComment());
            stmt.setTimestamp(4, new Timestamp(comment.getCommentedOn().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Comment> getComments(String game){
        List<Comment> results = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
             PreparedStatement stmt = connection.prepareStatement(SELECT_COMMAND);
        ) {
            stmt.setString(1, game);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment(
                            rs.getString(1), rs.getString(2),
                            rs.getString(3), rs.getTimestamp(4));
                    results.add(comment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

}
