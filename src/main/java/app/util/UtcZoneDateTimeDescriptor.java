package app.util;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UtcZoneDateTimeDescriptor extends AbstractTypeDescriptor<ZonedDateTime> {

    /**
     * Singleton access
     */
    public static final UtcZoneDateTimeDescriptor INSTANCE = new UtcZoneDateTimeDescriptor();

    @SuppressWarnings("unchecked")
    public UtcZoneDateTimeDescriptor() {
        super(ZonedDateTime.class, ImmutableMutabilityPlan.INSTANCE );
    }

    @Override
    public String toString(ZonedDateTime value) {
        return UtcZoneDateTimeType.FORMATTER.format(value.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
    }

    @Override
    public ZonedDateTime fromString(String string) {
        return ZonedDateTime.from(UtcZoneDateTimeType.FORMATTER.parse(string)).withZoneSameInstant(ZoneId.of("UTC"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(ZonedDateTime zonedDateTime, Class<X> type, WrapperOptions options) {
        if ( zonedDateTime == null ) {
            return null;
        }

        if ( ZonedDateTime.class.isAssignableFrom( type ) ) {
            return (X) zonedDateTime;
        }

        if ( Calendar.class.isAssignableFrom( type ) ) {
            return (X) GregorianCalendar.from( zonedDateTime );
        }

        if ( Timestamp.class.isAssignableFrom( type ) ) {
            return (X) Timestamp.from( zonedDateTime.toInstant() );
        }

        if ( java.sql.Date.class.isAssignableFrom( type ) ) {
            return (X) java.sql.Date.from( zonedDateTime.toInstant() );
        }

        if ( java.sql.Time.class.isAssignableFrom( type ) ) {
            return (X) java.sql.Time.from( zonedDateTime.toInstant() );
        }

        if ( Date.class.isAssignableFrom( type ) ) {
            return (X) Date.from( zonedDateTime.toInstant() );
        }

        if ( Long.class.isAssignableFrom( type ) ) {
            return (X) Long.valueOf( zonedDateTime.toInstant().toEpochMilli() );
        }

        throw unknownUnwrap( type );
    }

    @Override
    public <X> ZonedDateTime wrap(X value, WrapperOptions options) {
        if ( value == null ) {
            return null;
        }

        if ( ZonedDateTime.class.isInstance( value ) ) {
            return (ZonedDateTime) value;
        }

        if ( java.sql.Timestamp.class.isInstance( value ) ) {
            final Timestamp ts = (Timestamp) value;
            return ZonedDateTime.ofInstant( ts.toInstant(), ZoneId.systemDefault() );
        }

        if ( java.util.Date.class.isInstance( value ) ) {
            final java.util.Date date = (java.util.Date) value;
            return ZonedDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() );
        }

        if ( Long.class.isInstance( value ) ) {
            return ZonedDateTime.ofInstant( Instant.ofEpochMilli( (Long) value ), ZoneId.systemDefault() );
        }

        if ( Calendar.class.isInstance( value ) ) {
            final Calendar calendar = (Calendar) value;
            return ZonedDateTime.ofInstant( calendar.toInstant(), calendar.getTimeZone().toZoneId() );
        }

        throw unknownWrap( value.getClass() );
    }
}
