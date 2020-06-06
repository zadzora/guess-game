package sk.tuke.gamestudio.service;



import sk.tuke.gamestudio.entity.Comment;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentServiceFIle implements CommentService {
    private static final String FILE_NAME = "comment.db";

    @Override
    public void addComment(Comment comment) {
        List<Comment> comments = loadComment();
        comments.add(comment);
      //  Collections.sort(comments);
        saveComment(comments);
    }

    @Override
    public List<Comment> getComments(String game) {
        return null;
    }


    private void saveComment(List<Comment> scores) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            os.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Comment> loadComment() {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Comment>) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
