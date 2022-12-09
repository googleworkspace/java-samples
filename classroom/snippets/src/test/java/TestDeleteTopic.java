import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.model.Topic;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class TestDeleteTopic extends BaseTest {
  @Test
  public void testDeleteTopic() throws IOException {
    Topic topic = CreateTopic.createTopic(testCourse.getId());
    DeleteTopic.deleteTopic(testCourse.getId(), topic.getTopicId());
    Assert.assertThrows(GoogleJsonResponseException.class,
        () -> GetTopic.getTopic(testCourse.getId(), topic.getTopicId()));
  }
}
