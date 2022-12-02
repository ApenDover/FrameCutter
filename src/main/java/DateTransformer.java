import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DateTransformer {

    static final Logger logger = LoggerFactory.getLogger(DateTransformer.class);
    public static long getMsFromDate(String errorDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        Date date = null;
        try {
            date = sdf.parse(errorDate);
        } catch (ParseException e) {
            logger.error(e.getMessage() + " :: " + Arrays.toString(e.getStackTrace()));
        }
        return date.getTime();
    }

    public static String getDateFromMs(Long mSeconds) {
        Date date = new Date(mSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.format(date);
        return sdf.format(date);
    }
}