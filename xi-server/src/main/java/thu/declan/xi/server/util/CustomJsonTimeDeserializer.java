/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thu.declan.xi.server.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Time;

/**
 *
 * @author declan
 */
public class CustomJsonTimeDeserializer extends JsonDeserializer<Time> {

	@Override
	public Time deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String date = jp.getText();
		try {
			return new Time(format.parse(date).getTime());
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
