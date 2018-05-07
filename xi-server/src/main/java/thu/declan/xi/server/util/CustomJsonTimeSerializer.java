package thu.declan.xi.server.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author declan
 */
public class CustomJsonTimeSerializer extends JsonSerializer<Time> {

	@Override
	public void serialize(Time value, JsonGenerator jgen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		String formattedDate = formatter.format(new Date(value.getTime()));
		jgen.writeString(formattedDate);
	}
	
}
