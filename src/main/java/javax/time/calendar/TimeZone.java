/*
 * Copyright (c) 2007-2009, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar;

import static javax.time.calendar.LocalTime.*;
import static javax.time.calendar.field.DayOfWeek.*;
import static javax.time.calendar.field.MonthOfYear.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.time.Instant;
import javax.time.calendar.ZoneRulesBuilder.TimeDefinition;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.Year;
import javax.time.period.Period;

/**
 * A time zone representing the set of rules by which the zone offset
 * varies through the year and historically.
 * <p>
 * TimeZone is an abstract class and must be implemented with care
 * to ensure other classes in the framework operate correctly.
 * All instantiable implementations must be final, immutable and thread-safe.
 * It is only intended that the abstract methods are overridden.
 *
 * @author Stephen Colebourne
 */
public abstract class TimeZone implements Serializable {

    /**
     * A serialization identifier for this class.
     */
    private static final long serialVersionUID = 93618758758127L;
    /**
     * Cache of time zones by id.
     */
    private static final ConcurrentMap<String, TimeZone> CACHE = new ConcurrentHashMap<String, TimeZone>();
    /**
     * The time zone offset for UTC, with an id of 'UTC'.
     */
    public static final TimeZone UTC = timeZone(ZoneOffset.UTC);

    /**
     * The time zone ID.
     */
    private final String timeZoneID;

    //-----------------------------------------------------------------------
    /**
     * Obtains an instance of <code>TimeZone</code> using its ID using a map
     * of aliases to supplement the standard zone IDs.
     * <p>
     * Many users of time zones use short abbreviations, such as PST for
     * 'Pacific Standard Time' and PDT for 'Pacific Daylight Time'.
     * These abbreviations are not unique, and so cannot be used as identifiers.
     * This method allows a map of string to time zone to be setup and reused
     * within an application.
     *
     * @param timeZoneID  the time zone id, not null
     * @param aliasMap  a map of time zone ids (typically abbreviations) to time zones, not null
     * @return the TimeZone, never null
     */
    public static TimeZone timeZone(String timeZoneID, Map<String, TimeZone> aliasMap) {
        if (timeZoneID == null) {
            throw new NullPointerException("Time Zone ID must not be null");
        }
        if (aliasMap == null) {
            throw new NullPointerException("Alias map must not be null");
        }
        TimeZone zone = aliasMap.get(timeZoneID);
        return zone == null ? timeZone(timeZoneID) : zone;
    }

    /**
     * Obtains an instance of <code>TimeZone</code> using its ID.
     *
     * @param timeZoneID  the time zone id, not null
     * @return the TimeZone, never null
     */
    public static TimeZone timeZone(String timeZoneID) {
        if (timeZoneID == null) {
            throw new NullPointerException("Time Zone ID must not be null");
        }
        TimeZone zone = CACHE.get(timeZoneID);
        if (zone == null) {
            if (timeZoneID.startsWith("UTC") || timeZoneID.startsWith("GMT")) {  // not sure about GMT
                // 'UTC' will have been dealy with by the cache
                return timeZone(ZoneOffset.zoneOffset(timeZoneID.substring(3)));
            } else if (timeZoneID.equals("Europe/London")) {
                ZoneOffset offsetLMT = ZoneOffset.zoneOffset(0, -1, -15);
                ZoneOffset offset0 = ZoneOffset.zoneOffset(0);
                ZoneOffset offset1 = ZoneOffset.zoneOffset(1);
                Period oneHour = Period.hours(1);
                Period zeroHours = Period.ZERO;
                
                ZoneRulesBuilder b = new ZoneRulesBuilder(
                        offsetLMT, LocalDateTime.dateTime(1847, 12, 1, 0, 0), TimeDefinition.STANDARD);
                
                b.addWindow(offset0, LocalDateTime.dateTime(1968, 10, 27, 0, 0), TimeDefinition.WALL);
                b.addRuleToWindow(1916, MAY, 21, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1916, OCTOBER, 1, time(2, 0), TimeDefinition.STANDARD, zeroHours);
                b.addRuleToWindow(1917, APRIL, 8, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1917, SEPTEMBER, 17, time(2, 0), TimeDefinition.STANDARD, zeroHours);
                b.addRuleToWindow(1918, MARCH, 24, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1918, SEPTEMBER, 30, time(2, 0), TimeDefinition.STANDARD, zeroHours);
                b.addRuleToWindow(1919, MARCH, 30, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1919, SEPTEMBER, 29, time(2, 0), TimeDefinition.STANDARD, zeroHours);
                b.addRuleToWindow(1920, MARCH, 28, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1920, OCTOBER, 25, time(2, 0), TimeDefinition.STANDARD, zeroHours);
                // ...
                b.addRuleToWindow(1961, 1963, MARCH, -1, SUNDAY, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1961, 1968, OCTOBER, 23, SUNDAY, time(2, 0), TimeDefinition.STANDARD, zeroHours);
                b.addRuleToWindow(1964, 1967, MARCH, 19, SUNDAY, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1968, FEBRUARY, 18, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1972, 1980, MARCH, 16, SUNDAY, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1972, 1980, OCTOBER, 23, SUNDAY, time(2, 0), TimeDefinition.STANDARD, zeroHours);
                
                b.addWindow(offset1, LocalDateTime.dateTime(1971, 10, 31, 2, 0), TimeDefinition.UTC);
                
                b.addWindow(offset0, LocalDateTime.dateTime(1996, 1, 1, 0, 0), TimeDefinition.WALL);
                b.addRuleToWindow(1968, FEBRUARY, 18, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1972, 1980, MARCH, 16, SUNDAY, time(2, 0), TimeDefinition.STANDARD, oneHour);
                b.addRuleToWindow(1972, 1980, OCTOBER, 23, SUNDAY, time(2, 0), TimeDefinition.STANDARD, zeroHours);
                b.addRuleToWindow(1981, 1995, MARCH, -1, SUNDAY, time(1, 0), TimeDefinition.UTC, oneHour);
                b.addRuleToWindow(1981, 1989, OCTOBER, 23, SUNDAY, time(1, 0), TimeDefinition.UTC, zeroHours);
                b.addRuleToWindow(1990, 1995, OCTOBER, 22, SUNDAY, time(1, 0), TimeDefinition.UTC, zeroHours);
                
                b.addWindowForever(offset0);
                b.addRuleToWindow(1996, Year.MAX_YEAR, MARCH, -1, SUNDAY, time(1, 0), TimeDefinition.UTC, oneHour);
                b.addRuleToWindow(1996, Year.MAX_YEAR, OCTOBER, -1, SUNDAY, time(1, 0), TimeDefinition.UTC, zeroHours);
                TimeZone london = b.toRules(timeZoneID);
                TimeZone cached = CACHE.putIfAbsent(timeZoneID, london);
                zone = (cached != null ? cached : london);
            } else {
                // TODO: proper zone provider
                Map<String, Integer> map = new HashMap<String, Integer>();
                map.put("Europe/Dublin", 0);
                map.put("Europe/Lisbon", 0);
                map.put("Europe/London", 0);
                map.put("Europe/Amsterdam", 1);
                map.put("Europe/Andorra", 1);
                map.put("Europe/Belgrade", 1);
                map.put("Europe/Ljubljana", 1);
                map.put("Europe/Podgorica", 1);
                map.put("Europe/Sarajevo", 1);
                map.put("Europe/Skopje", 1);
                map.put("Europe/Zagreb", 1);
                map.put("Europe/Berlin", 1);
                map.put("Europe/Brussels", 1);
                map.put("Europe/Budapest", 1);
                map.put("Europe/Copenhagen", 1);
                map.put("Europe/Gibraltar", 1);
                map.put("Europe/Luxembourg", 1);
                map.put("Europe/Madrid", 1);
                map.put("Europe/Malta", 1);
                map.put("Europe/Monaco", 1);
                map.put("Europe/Oslo", 1);
                map.put("Europe/Paris", 1);
                map.put("Europe/Prague", 1);
                map.put("Europe/Bratislava", 1);
                map.put("Europe/Rome", 1);
                map.put("Europe/San_Marino", 1);
                map.put("Europe/Vatican", 1);
                map.put("Europe/Stockholm", 1);
                map.put("Europe/Tirane", 1);
                map.put("Europe/Vaduz", 1);
                map.put("Europe/Vienna", 1);
                map.put("Europe/Warsaw", 1);
                map.put("Europe/Zurich", 1);
                map.put("Europe/Athens", 2);
                map.put("Europe/Bucharest", 2);
                map.put("Europe/Chisinau", 2);
                map.put("Europe/Helsinki", 2);
                map.put("Asia/Istanbul", 2);
                map.put("Europe/Istanbul", 2);
                map.put("Europe/Kaliningrad", 2);
                map.put("Europe/Kiev", 2);
                map.put("Europe/Minsk", 2);
                map.put("Europe/Riga", 2);
                map.put("Europe/Sofia", 2);
                map.put("Europe/Tallinn", 2);
                map.put("Europe/Vilnius", 2);
                Integer standardOffset = map.get(timeZoneID);
                if (standardOffset != null) {
                    zone = new EuropeZone(timeZoneID, standardOffset);
                } else {
                    map = new HashMap<String, Integer>();
                    map.put("America/New_York", -5);
                    map.put("America/Toronto", -5);
                    map.put("America/Chicago", -6);
                    map.put("America/Winnipeg", -6);
                    map.put("America/Denver", -7);
                    map.put("America/Edmonton", -7);
                    map.put("America/Los_Angeles", -8);
                    map.put("America/Vancouver", -8);
                    standardOffset = map.get(timeZoneID);
                    if (standardOffset != null) {
                        zone = new AmericaZone(timeZoneID, standardOffset);
                    } else {
                        throw new IllegalArgumentException("Unsupported time zone: " + timeZoneID);
                    }
                }
                TimeZone cached = CACHE.putIfAbsent(timeZoneID, zone);
                zone = (cached != null ? cached : zone);
            }
        }
        return zone;
    }

    /**
     * Obtains an instance of <code>TimeZone</code> using an offset.
     *
     * @param offset  the zone offset, not null
     * @return the TimeZone for the offset, never null
     */
    public static TimeZone timeZone(ZoneOffset offset) {
        if (offset == null) {
            throw new NullPointerException("ZoneOffset must not be null");
        }
        String timeZoneID = (offset == ZoneOffset.UTC ? "UTC" : "UTC" + offset.getID());
        TimeZone zone = CACHE.get(timeZoneID);
        if (zone == null) {
            zone = new Fixed(timeZoneID, offset);
            TimeZone cached = CACHE.putIfAbsent(timeZoneID, zone);
            zone = (cached != null ? cached : zone);
        }
        return zone;
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Gets a list of all the available zone IDs.
//     *
//     * @return a list of available time zone IDs
//     */
//    public static List<String> getAvailableIDs() {
//        return new ArrayList<String>();  // TODO
//    }
//
//    /**
//     * Gets a list of all the available zone IDs matching the standard zone offset.
//     * <p>
//     * This method is useful for finding all those zones that have the same offset
//     * (standard/winter) but different daylight savings behaviour.
//     *
//     * @param standardOffset  the offset to find
//     * @return a list of available time zone IDs
//     */
//    public static List<String> getAvailableIDs(ZoneOffset standardOffset) {
//        return new ArrayList<String>();  // TODO
//    }

    //-----------------------------------------------------------------------
    /**
     * Constructs an instance using the time zone ID.
     *
     * @param timeZoneID  the time zone id, not null
     */
    protected TimeZone(String timeZoneID) {
        super();
        this.timeZoneID = timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the time zone ID.
     *
     * @return the time zone ID, never null
     */
    public final String getID() {
        return timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the textual name of this zone.
     *
     * @return the time zone name, never null
     */
    public String getName() {
        return timeZoneID;  // TODO
    }

    /**
     * Gets the short textual name of this zone.
     *
     * @return the time zone short name, never null
     */
    public String getShortName() {
        return timeZoneID;  // TODO
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the offset applicable at the specified instant in this zone.
     * <p>
     * For any given instant there can only ever be one valid offset, which
     * is returned by this method. To access more detailed information about
     * the offset at and around the instant use {@link #getOffsetInfo(Instant)}.
     *
     * @param instant  the instant to find the offset for, not null
     * @return the offset, never null
     */
    public abstract ZoneOffset getOffset(Instant instant);

    /**
     * Gets the offset information for the specified instant in this zone.
     * <p>
     * This provides access to full details as to the offset or offsets applicable
     * for the local date-time. The mapping from an instant to an offset
     * is not straightfoward. There are two cases:
     * <ul>
     * <li>Normal. Where there is a single offset for the local date-time.</li>
     * <li>Overlap. Where there is a gap in the local time-line normally caused by the
     * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
     * </ul>
     * The third case, a gap in the local time-line, cannot be returned by this
     * method as an instant will always represent a valid point and cannot be in a gap.
     * The returned object provides information about the offset or overlap and it
     * is vital to check {@link OffsetInfo#isDiscontinuity()} to handle the overlap.
     *
     * @param instant  the instant to find the offset information for, not null
     * @return the offset information, never null
     */
    public OffsetInfo getOffsetInfo(Instant instant) {
        ZoneOffset offset = getOffset(instant);
        OffsetDateTime odt = OffsetDateTime.dateTime(instant, offset);
        return getOffsetInfo(odt.toLocalDateTime());
    }

    /**
     * Gets the offset information for a local date-time in this zone.
     * <p>
     * This provides access to full details as to the offset or offsets applicable
     * for the local date-time. The mapping from a local date-time to an offset
     * is not straightfoward. There are three cases:
     * <ul>
     * <li>Normal. Where there is a single offset for the local date-time.</li>
     * <li>Gap. Where there is a gap in the local time-line normally caused by the
     * spring cutover to daylight savings. There are no valid offsets within the gap</li>
     * <li>Overlap. Where there is a gap in the local time-line normally caused by the
     * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
     * </ul>
     * The returned object provides this information and it is vital to check
     * {@link OffsetInfo#isDiscontinuity()} to handle the gap or overlap.
     *
     * @param dateTime  the date-time to find the offset information for, not null
     * @return the offset information, never null
     */
    public abstract OffsetInfo getOffsetInfo(LocalDateTime dateTime);

    /**
     * Is this time zone fixed, such that the offset never varies.
     * <p>
     * It is intended that {@link OffsetDateTime}, {@link OffsetDate} and
     * {@link OffsetTime} are used in preference to fixed offset time zones
     * in {@link ZonedDateTime}.
     * <p>
     * The default implementation returns false and it is not intended that
     * user-supplied subclasses override this.
     *
     * @return true if the time zone is fixed and the offset never changes
     */
    public boolean isFixed() {
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Creates an offset info for the normal case where only one offset is valid.
     *
     * @param dateTime  the date-time that this info applies to, not null
     * @param offset  the zone offset, not null
     * @return the created offset info, never null
     */
    protected OffsetInfo createOffsetInfo(LocalDateTime dateTime, ZoneOffset offset) {
        if (dateTime == null) {
            throw new NullPointerException("LocalDateTime must not be null");
        }
        if (offset == null) {
            throw new NullPointerException("ZoneOffset must not be null");
        }
        return new OffsetInfo(dateTime, offset);
    }

    /**
     * Constructor for a gap where there are no valid offsets.
     *
     * @param dateTime  the date-time that this info applies to, not null
     * @param cutoverDateTime  the date-time of the discontinuity using the offset before, not null
     * @param offsetAfter  the offset after the discontinuity, not null
     * @return the created offset info, never null
     */
    protected OffsetInfo createOffsetInfo(
            LocalDateTime dateTime, OffsetDateTime cutoverDateTime, ZoneOffset offsetAfter) {
        if (dateTime == null) {
            throw new NullPointerException("LocalDateTime must not be null");
        }
        if (cutoverDateTime == null) {
            throw new NullPointerException("OffsetDateTime must not be null");
        }
        if (offsetAfter == null) {
            throw new NullPointerException("ZoneOffset must not be null");
        }
        return new OffsetInfo(dateTime, cutoverDateTime, offsetAfter);
    }

    //-----------------------------------------------------------------------
    /**
     * Is this instance equal to that specified by comparing the ID.
     *
     * @param otherZone  the other zone, null returns false
     * @return true if this zone is the same as that specified
     */
    @Override
    public boolean equals(Object otherZone) {
        if (this == otherZone) {
           return true;
        }
        if (otherZone instanceof TimeZone) {
            return timeZoneID.equals(((TimeZone) otherZone).timeZoneID);
        }
        return false;
    }

    /**
     * A hashcode for the time zone object.
     *
     * @return a suitable hashcode
     */
    @Override
    public int hashCode() {
        return timeZoneID.hashCode();
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a string representation of the time zone using the ID.
     *
     * @return the time zone ID, never null
     */
    @Override
    public String toString() {
        return timeZoneID;
    }

    //-----------------------------------------------------------------------
    /**
     * Information about a discontinuity in the local time-line.
     * <p>
     * A discontinuity is normally the result of daylight savings cutovers,
     * where a gap occurs in spring and an overlap occurs in autumn.
     * <p>
     * Discontinuity is immutable and thread-safe.
     *
     * @author Stephen Colebourne
     */
    public static final class Discontinuity {
        /** The transition date-time with the offset before the discontinuity. */
        private final OffsetDateTime transition;
        /** The offset at and after the discontinuity. */
        private final ZoneOffset offsetAfter;
        
        /**
         * Constructor.
         *
         * @param transition  the transition date-time with the offset before the discontinuity, not null
         * @param offsetAfter  the offset at and after the discontinuity, not null
         */
        private Discontinuity(OffsetDateTime transition, ZoneOffset offsetAfter) {
            this.transition = transition;
            this.offsetAfter = offsetAfter;
        }
        
        //-----------------------------------------------------------------------
        /**
         * Gets the transition instant.
         * This is the first instant after the discontinuity, when the new offset applies.
         *
         * @return the transition instant, not null
         */
        public Instant getTransitionInstant() {
            return transition.toInstant();
        }
        
        /**
         * Gets the transition date-time expressed with the before offset.
         * This is the date-time where the discontinuity begins, and as such it never
         * actually occurs.
         *
         * @return the transition date-time expressed with the before offset, not null
         */
        public OffsetDateTime getTransitionDateTime() {
            return transition;
        }
        
        /**
         * Gets the transition date-time expressed with the after offset.
         * This is the first date-time after the discontinuity, when the new offset applies.
         *
         * @return the transition date-time expressed with the after offset, not null
         */
        public OffsetDateTime getTransitionDateTimeAfter() {
            return transition.adjustLocalDateTime(offsetAfter);
        }
        
        /**
         * Gets the offset before the gap.
         *
         * @return the offset before the gap, not null
         */
        public ZoneOffset getOffsetBefore() {
            return transition.getOffset();
        }
        
        /**
         * Gets the offset after the gap.
         *
         * @return the offset after the gap, not null
         */
        public ZoneOffset getOffsetAfter() {
            return offsetAfter;
        }
        
        /**
         * Gets the size of the discontinuity in seconds.
         *
         * @return the size of the discontinuity in seconds, positive for gaps, negative for overlaps
         */
        public Period getDiscontinuitySize() {
            int secs = getOffsetAfter().getAmountSeconds() - getOffsetBefore().getAmountSeconds();
            return Period.seconds(secs).normalized();
        }
        
        /**
         * Does this discontinuity represent a gap in the local time-line.
         *
         * @return true if this discontinuity is a gap
         */
        public boolean isGap() {
            return getOffsetAfter().getAmountSeconds() > getOffsetBefore().getAmountSeconds();
        }
        
        /**
         * Does this discontinuity represent a gap in the local time-line.
         *
         * @return true if this discontinuity is an overlap
         */
        public boolean isOverlap() {
            return getOffsetAfter().getAmountSeconds() < getOffsetBefore().getAmountSeconds();
        }
        
//        /**
//         * Checks if the specified offset is one of those described by this discontinuity.
//         *
//         * @param offset  the offset to check, null returns false
//         * @return true if the offset is one of those described by this discontinuity
//         */
//        public boolean containsOffset(ZoneOffset offset) {
//            return offsetBefore.equals(offset) || offsetAfter.equals(offset);
//        }
        
        /**
         * Checks if the specified offset is valid during this discontinuity.
         * A gap will always return false.
         * An overlap will return true if the offset is either the before or after offset.
         *
         * @param offset  the offset to check, null returns false
         * @return true if the offset is valid during the discontinuity
         */
        public boolean isValidOffset(ZoneOffset offset) {
            return isGap() ? false : (getOffsetBefore().equals(offset) || getOffsetAfter().equals(offset));
        }
        
        //-----------------------------------------------------------------------
        /**
         * Checks if this instance equals another.
         *
         * @param other  the other object to compare to, null returns false
         * @return true if equal
         */
        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (other instanceof Discontinuity) {
                Discontinuity d = (Discontinuity) other;
                return transition.equals(d.transition) &&
                    offsetAfter.equals(d.offsetAfter);
            }
            return false;
        }
        
        /**
         * Gets the hash code.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            return transition.hashCode() ^ offsetAfter.hashCode();
        }
        
        /**
         * Gets a string describing this object.
         *
         * @return a string for debugging, never null
         */
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("Discontinuity[")
                .append(isGap() ? "Gap" : "Overlap")
                .append(" at ")
                .append(transition)
                .append(" to ")
                .append(offsetAfter)
                .append(']');
            return buf.toString();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Information about the valid offsets applicable for a local date-time.
     * <p>
     * The mapping from a local date-time to an offset is not straightfoward.
     * There are three cases:
     * <ul>
     * <li>Normal. Where there is a single offset for the local date-time.</li>
     * <li>Gap. Where there is a gap in the local time-line normally caused by the
     * spring cutover to daylight savings. There are no valid offsets within the gap</li>
     * <li>Overlap. Where there is a gap in the local time-line normally caused by the
     * autumn cutover from daylight savings. There are two valid offsets during the overlap.</li>
     * </ul>
     * When using this class, it is vital to check the {@link #isDiscontinuity()}
     * method to handle the gap and overlap. Alternatively use one of the general
     * methods {@link #getEstimatedOffset()} or {@link #isValidOffset(ZoneOffset)}.
     * <p>
     * OffsetInfo is immutable and thread-safe.
     *
     * @author Stephen Colebourne
     */
    public static class OffsetInfo {
        /** The date-time that this info applies to. */
        private final LocalDateTime dateTime;
        /** The offset for the local time-line. */
        private final ZoneOffset offset;
        /** The discontinuity in the local time-line. */
        private final Discontinuity discontinuity;
        
        /**
         * Constructor for handling a simple single offset.
         *
         * @param dateTime  the date-time that this info applies to, not null
         * @param offset  the offset applicable at the date-time, not null
         */
        private OffsetInfo(
                LocalDateTime dateTime,
                ZoneOffset offset) {
            this.dateTime = dateTime;
            this.offset = offset;
            this.discontinuity = null;
        }
        
        /**
         * Constructor for handling a discontinuity.
         *
         * @param dateTime  the date-time that this info applies to, not null
         * @param cutoverDateTime  the date-time of the cutover with the offset before, not null
         * @param offsetAfter  the offset applicable after the cutover gap/overlap, not null
         */
        private OffsetInfo(
                LocalDateTime dateTime,
                OffsetDateTime cutoverDateTime,
                ZoneOffset offsetAfter) {
            this.dateTime = dateTime;
            this.offset = null;
            this.discontinuity = new Discontinuity(cutoverDateTime, offsetAfter);
        }
        
        //-----------------------------------------------------------------------
        /**
         * Gets the local date-time that this info is applicable to.
         *
         * @return true if there is no valid offset
         */
        public LocalDateTime getLocalDateTime() {
            return dateTime;
        }
        
        /**
         * Is the offset information for the local date-time a discontinuity.
         * A discontinuity may be a gap or overlap and is normally caused by
         * daylight savings cutover.
         *
         * @return true if there is a discontinuity in the local time-line
         */
        public boolean isDiscontinuity() {
            return discontinuity != null;
        }
        
        /**
         * Gets information about the offset for the local time-line.
         * This method should only be called after calling {@link #isDiscontinuity()}.
         *
         * @return true if there is a single valid offset
         */
        public ZoneOffset getOffset() {
            return offset;
        }
        
        /**
         * Gets information about any discontinuity in the local time-line.
         * This method should only be called after calling {@link #isDiscontinuity()}.
         *
         * @return the discontinuity in the local-time line, null if not a discontinuity
         */
        public Discontinuity getDiscontinuity() {
            return discontinuity;
        }
        
        //-----------------------------------------------------------------------
        /**
         * Gets an estimated offset for the local date-time.
         * <p>
         * The result will be the same as {@link #getOffset()} except during a discontinuity.
         * During a discontinuity, the value of {@link Discontinuity#getOffsetAfter()} will
         * be returned. How meaningful that offset is depends on your application.
         *
         * @return a suitable estimated offset, never null
         */
        public ZoneOffset getEstimatedOffset() {
            return isDiscontinuity() ? getDiscontinuity().getOffsetAfter() : offset;
        }
        
//        /**
//         * Checks if the zone offset is valid for this local date-time.
//         *
//         * @param offset  the zone offset to check, not null
//         * @return the list of offsets from earliest to latest, never null
//         */
//        public boolean containsOffset(ZoneOffset offset) {
//            return isDiscontinuity() ? discontinuity.containsOffset(offset) : this.offset.equals(offset);
//        }
        
        /**
         * Checks if the specified offset is valid for this discontinuity.
         *
         * @param offset  the offset to check, null returns false
         * @return true if the offset is one of those described by this discontinuity
         */
        public boolean isValidOffset(ZoneOffset offset) {
            return isDiscontinuity() ? discontinuity.isValidOffset(offset) : this.offset.equals(offset);
        }
        
        //-----------------------------------------------------------------------
        /**
         * Gets a string describing this object.
         *
         * @return a string for debugging, never null
         */
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("OffsetInfo[")
                .append(isDiscontinuity() ? discontinuity : offset)
                .append(']');
            return buf.toString();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of time zone for fixed offsets.
     */
    private static final class Fixed extends TimeZone {
        /** The fixed offset. */
        private final ZoneOffset offset;
        /**
         * Constructor.
         * @param id  the time zone id, not null
         * @param offset  the zone offset, not null
         */
        private Fixed(String id, ZoneOffset offset) {
            super(id);
            this.offset = offset;
        }
        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return TimeZone.timeZone(getID());
        }
        /** {@inheritDoc} */
        @Override
        public ZoneOffset getOffset(Instant instant) {
            return offset;
        }
        /** {@inheritDoc} */
        @Override
        public OffsetInfo getOffsetInfo(LocalDateTime dateTime) {
            return new OffsetInfo(dateTime, offset);
        }
        /** {@inheritDoc} */
        @Override
        public boolean isFixed() {
            return true;
        }
    }

//    //-----------------------------------------------------------------------
//    /**
//     * Implementation of time zone based on java.util.TimeZone.
//     */
//    private static final class UtilZone extends TimeZone {
//        /** The fixed offset. */
//        private final java.util.TimeZone utilZone;
//        /**
//         * Constructor.
//         * @param id  the time zone id, not null
//         * @param utilZone  the java.util.TimeZone instance, not null
//         */
//        private UtilZone(String id, java.util.TimeZone utilZone) {
//            super(id);
//            this.utilZone = utilZone;
//        }
//        /**
//         * Resolves singletons.
//         * @return the singleton instance
//         */
//        private Object readResolve() {
//            return TimeZone.timeZone(getID());
//        }
//        /** {@inheritDoc} */
//        @Override
//        public ZoneOffset getOffset(Instant instant) {
//            int offsetMillis = utilZone.getOffset(instant.toEpochMillis());
//            return ZoneOffset.forTotalSeconds(offsetMillis / 1000);
//        }
//        /** {@inheritDoc} */
//        @Override
//        public OffsetInfo getOffsetInfo(LocalDateTime dateTime) {
////            // TODO: better algorithm / overflows
////            long wallMillis = dateTime.toLocalDate().toModifiedJulianDays() * 24 * 60 * 60 * 1000 +
////                    dateTime.toLocalTime().toMilliOfDay();
////            int offsetMillis = utilZone.getOffset(wallMillis);
////            long millis = wallMillis - offsetMillis;
////            return ZoneOffset.forTotalSeconds(offsetMillis / 1000);
//            int offsetMillis = utilZone.getOffset(
//                    GregorianCalendar.BC,
//                    dateTime.getYear().getValue(),
//                    dateTime.getMonthOfYear().getValue() - 1,
//                    dateTime.getDayOfMonth().getValue(),
//                    Calendar.SUNDAY,
//                    dateTime.toLocalTime().toMilliOfDay());
//            return ZoneOffset.forTotalSeconds(offsetMillis / 1000);
//        }
//        /** {@inheritDoc} */
//        @Override
//        public boolean isFixed() {
//            return utilZone.useDaylightTime();
//        }
//    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of European rule time zones.
     */
    private static final class EuropeZone extends TimeZone {
        /** The standard offset in hours. */
        private final ZoneOffset standardOffset;
        /** The standard offset in hours. */
        private final ZoneOffset summerOffset;
        /**
         * Constructor.
         * @param id  the time zone id, not null
         * @param utilZone  the java.util.TimeZone instance, not null
         */
        private EuropeZone(String id, int standardOffset) {
            super(id);
            this.standardOffset = ZoneOffset.zoneOffset(standardOffset);
            this.summerOffset = ZoneOffset.zoneOffset(standardOffset + 1);
        }
        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return TimeZone.timeZone(getID());
        }
        /** {@inheritDoc} */
        @Override
        public ZoneOffset getOffset(Instant instant) {
            OffsetDateTime dt = OffsetDateTime.dateTime(instant, ZoneOffset.UTC);
            ZoneOffset offsetBefore = standardOffset;
            ZoneOffset offsetAfter = standardOffset;
            switch (dt.getMonthOfYear()) {
                case JANUARY:
                case FEBRUARY:
                case NOVEMBER:
                case DECEMBER:
                    return standardOffset;
                case MARCH:
                    offsetAfter = summerOffset;
                    break;
                case APRIL:
                case MAY:
                case JUNE:
                case JULY:
                case AUGUST:
                case SEPTEMBER:
                    return summerOffset;
                case OCTOBER:
                    offsetBefore = summerOffset;
                    break;
            }
            int dom = dt.getDayOfMonth().getValue();
            if (dom < 25) {
                return offsetBefore;
            }
            if (dt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                OffsetDateTime cutover = OffsetDateTime.dateTime(dt.toLocalDate(), LocalTime.time(1, 0), ZoneOffset.UTC);
                return instant.isBefore(cutover.toInstant()) ? offsetBefore : offsetAfter;
            }
            int daysToSun = 7 - dt.getDayOfWeek().getValue();
            return dom + daysToSun <= 31 ? offsetBefore : offsetAfter;
        }
        /** {@inheritDoc} */
        @Override
        public OffsetInfo getOffsetInfo(LocalDateTime dt) {
            ZoneOffset offsetBefore = standardOffset;
            ZoneOffset offsetAfter = standardOffset;
            switch (dt.getMonthOfYear()) {
                case JANUARY:
                case FEBRUARY:
                case NOVEMBER:
                case DECEMBER:
                    return new OffsetInfo(dt, standardOffset);
                case MARCH:
                    offsetAfter = summerOffset;
                    break;
                case APRIL:
                case MAY:
                case JUNE:
                case JULY:
                case AUGUST:
                case SEPTEMBER:
                    return new OffsetInfo(dt, summerOffset);
                case OCTOBER:
                    offsetBefore = summerOffset;
                    break;
            }
            int dom = dt.getDayOfMonth().getValue();
            if (dom < 25) {
                return new OffsetInfo(dt, offsetBefore);
            }
            if (dt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                if (dt.getHourOfDay().getValue() < 1 + standardOffset.getHoursField()) {
                    return new OffsetInfo(dt, offsetBefore);
                }
                if (dt.getHourOfDay().getValue() >= 2 + standardOffset.getHoursField()) {
                    return new OffsetInfo(dt, offsetAfter);
                }
                OffsetDateTime cutover = OffsetDateTime.dateTime(dt.toLocalDate(), LocalTime.time(1, 0), ZoneOffset.UTC);
                return new OffsetInfo(dt, cutover.adjustLocalDateTime(offsetBefore), offsetAfter);
            }
            int daysToSun = 7 - dt.getDayOfWeek().getValue();
            return dom + daysToSun <= 31 ? new OffsetInfo(dt, offsetBefore) : new OffsetInfo(dt, offsetAfter);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Implementation of American rule time zones.
     */
    private static final class AmericaZone extends TimeZone {
        /** The standard offset in hours. */
        private final ZoneOffset standardOffset;
        /** The standard offset in hours. */
        private final ZoneOffset summerOffset;
        /**
         * Constructor.
         * @param id  the time zone id, not null
         * @param utilZone  the java.util.TimeZone instance, not null
         */
        private AmericaZone(String id, int standardOffset) {
            super(id);
            this.standardOffset = ZoneOffset.zoneOffset(standardOffset);
            this.summerOffset = ZoneOffset.zoneOffset(standardOffset + 1);
        }
        /**
         * Resolves singletons.
         * @return the singleton instance
         */
        private Object readResolve() {
            return TimeZone.timeZone(getID());
        }
        /** {@inheritDoc} */
        @Override
        public ZoneOffset getOffset(Instant instant) {
            OffsetDateTime dt = OffsetDateTime.dateTime(instant, standardOffset);
            switch (dt.getMonthOfYear()) {
                case JANUARY:
                case FEBRUARY:
                case DECEMBER:
                    return standardOffset;
                case MARCH: {
                    int dom = dt.getDayOfMonth().getValue();
                    if (dom < 8) {
                        return standardOffset;
                    }
                    if (dom > 14) {
                        return summerOffset;
                    }
                    if (dt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        OffsetDateTime cutover = OffsetDateTime.dateTime(dt.toLocalDate(), LocalTime.time(2, 0), standardOffset);
                        return instant.isBefore(cutover.toInstant()) ? standardOffset : summerOffset;
                    }
                    int daysToSun = 7 - dt.getDayOfWeek().getValue();
                    return dom + daysToSun <= 14 ? standardOffset : summerOffset;
                }
                case APRIL:
                case MAY:
                case JUNE:
                case JULY:
                case AUGUST:
                case SEPTEMBER:
                case OCTOBER:
                    return summerOffset;
                case NOVEMBER: {
                    int dom = dt.getDayOfMonth().getValue();
                    if (dom > 7) {
                        return standardOffset;
                    }
                    if (dt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        OffsetDateTime cutover = OffsetDateTime.dateTime(dt.toLocalDate(), LocalTime.time(2, 0), summerOffset);
                        return instant.isBefore(cutover.toInstant()) ? summerOffset : standardOffset;
                    }
                    int daysToSun = 7 - dt.getDayOfWeek().getValue();
                    return dom + daysToSun <= 7 ? summerOffset : standardOffset;
                }
            }
            throw new IllegalStateException();
        }
        /** {@inheritDoc} */
        @Override
        public OffsetInfo getOffsetInfo(LocalDateTime dt) {
            switch (dt.getMonthOfYear()) {
                case JANUARY:
                case FEBRUARY:
                case DECEMBER:
                    return new OffsetInfo(dt, standardOffset);
                case MARCH: {
                    int dom = dt.getDayOfMonth().getValue();
                    if (dom < 8) {
                        return new OffsetInfo(dt, standardOffset);
                    }
                    if (dom > 14) {
                        return new OffsetInfo(dt, summerOffset);
                    }
                    if (dt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        if (dt.getHourOfDay().getValue() < 2) {
                            return new OffsetInfo(dt, standardOffset);
                        }
                        if (dt.getHourOfDay().getValue() >= 3) {
                            return new OffsetInfo(dt, summerOffset);
                        }
                        OffsetDateTime cutover = OffsetDateTime.dateTime(dt.toLocalDate(), LocalTime.time(2, 0), standardOffset);
                        return new OffsetInfo(dt, cutover, summerOffset);
                    }
                    int daysToSun = 7 - dt.getDayOfWeek().getValue();
                    return dom + daysToSun <= 14 ? new OffsetInfo(dt, standardOffset) : new OffsetInfo(dt, summerOffset);
                }
                case APRIL:
                case MAY:
                case JUNE:
                case JULY:
                case AUGUST:
                case SEPTEMBER:
                case OCTOBER:
                    return new OffsetInfo(dt, summerOffset);
                case NOVEMBER: {
                    int dom = dt.getDayOfMonth().getValue();
                    if (dom > 7) {
                        return new OffsetInfo(dt, standardOffset);
                    }
                    if (dt.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        if (dt.getHourOfDay().getValue() < 1) {
                            return new OffsetInfo(dt, summerOffset);
                        }
                        if (dt.getHourOfDay().getValue() >= 2) {
                            return new OffsetInfo(dt, standardOffset);
                        }
                        OffsetDateTime cutover = OffsetDateTime.dateTime(dt.toLocalDate(), LocalTime.time(2, 0), summerOffset);
                        return new OffsetInfo(dt, cutover, standardOffset);
                    }
                    int daysToSun = 7 - dt.getDayOfWeek().getValue();
                    return dom + daysToSun <= 7 ? new OffsetInfo(dt, summerOffset) : new OffsetInfo(dt, standardOffset);
                }
            }
            throw new IllegalStateException();
        }
    }

}