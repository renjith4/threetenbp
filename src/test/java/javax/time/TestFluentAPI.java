/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time;

import static javax.time.calendar.DateAdjusters.*;
import static javax.time.calendar.LocalDate.*;
import static javax.time.calendar.field.DayOfMonth.*;
import static javax.time.calendar.field.DayOfWeek.*;
import static javax.time.calendar.field.HourOfDay.*;
import static javax.time.calendar.field.MinuteOfHour.*;
import static javax.time.calendar.field.MonthOfYear.*;
import static javax.time.calendar.field.Year.*;
import static javax.time.period.Period.*;

import javax.time.calendar.DateResolvers;
import javax.time.calendar.LocalDate;
import javax.time.calendar.LocalTime;
import javax.time.calendar.MonthDay;
import javax.time.calendar.field.AmPmOfDay;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.MonthOfYear;
import javax.time.period.Period;

/**
 * Test class.
 * 
 * @author Stephen Colebourne
 */
public class TestFluentAPI {

    public static void main(String[] args) {
        LocalTime tod = Clock.system().currentTime();
        tod.plusHours(6).plusMinutes(2);
        tod.plus(hours(6)).plus(minutes(2));
        if (tod.matches(AmPmOfDay.AM)) {
            tod = tod.with(hourOfDay(9));
        }
        
        LocalDate date = null;
        date = Clock.system().today().plusDays(3);
        date = Clock.system().today().plus(days(3));
//        date = Clock.system().today().plus(Days.days(3));
        
        date = date(2007, 3, 20);
        date = date(isoYear(2007), MARCH, dayOfMonth(20));
//        date = calendar().year(2007).december().dayOfMonth(20).buildLenient();
//        date = calendar().year(1972).december().dayOfMonth(3).build();
//        date = calendar().currentYear().december().dayOfMonth(20).buildLenient();
//        date = calendar().zoneID("Europe/London").year(2007).august().dayOfMonth(2).build();
//        
//        date = calendar().zoneID("America/New_York").year(2007).march().dayOfMonth(20).buildLenient();
//        date = calendar().defaultZone().year(2007).march().dayOfMonth(20).buildLenient();
        
        // date = LocalDate.date(clock().currentMonth(), dayOfMonth(6));
        
        date = date.with(lastDayOfMonth());
        date = date.with(nextNonWeekendDay());
//        date = date.with(nextMonday());
        date = date.with(next(MONDAY));
        date = date.with(nextOrCurrent(MONDAY));
        date = date.with(dayOfWeekInMonth(2, TUESDAY));
        date = date.with(firstInMonth(MONDAY));
        date = date.with(isoYear(2009));
        date = date.with(monthOfYear(6));
        date = date.with(MonthOfYear.AUGUST);
        MonthOfYear.DECEMBER.adjustDate(date, DateResolvers.strict());
        
        if (date.matches(FRIDAY) && date.matches(dayOfMonth(13))) {
            System.out.println("Spooky");
        }
        
        
//        date = date.with(MONDAY.adjustToNext());
        
        // different ways to build/use periods
        date = date.plus(yearsMonthsDays(2, 3, 1));
//        date = date.plus(3, YEARS).plus(2, MONTHS).plus(1, DAYS);
        date = date.plus(years(3)).plus(months(2)).plus(days(1));
//        PeriodFields d1 = periodBuilder().hours(2).seconds(3).build();
        Period d2 = hours(2).withSeconds(3);
        Period d3 = hours(2).plus(seconds(3));
        System.out.println(d2);
        System.out.println(d3);
        
        tod.with(hourOfDay(12)).with(minuteOfHour(30));
        tod.withHourOfDay(12).withMinuteOfHour(30);
        
//        if (tod.hourOfDay().getAmPm().isAm()) {
//            // handle morning
//        }
        
//        CORBADate c = null;
//        c.year = date.year().getValue();
//        c.month = date.month().getText(symbols);
//        c.day = date.day().getValue();
        
        //int q = date.get(QuarterOfYear.class);
        //int hourOfDay = HourOfDay.of(tod).get();
        
//        CalendarDateTime dt = CalendarDateTime.calendarDateTime(2007, february(), 21, 12, 30);
//        int sec = dt.withPrecisionAtLeastSeconds().getSecondOfMinute();
//        
//        CalendarDT<LocalTime> dtime = CalendarDT.calendarDateTime(2007, february(), 21, 12, 30);
//        int min = dtime.time().getMinuteOfHour();
        
        MonthDay md = MonthDay.monthDay(FEBRUARY, dayOfMonth(4));
        md = md.with(MARCH);
        md = md.rollDayOfMonth(3);
        
        DayOfMonth.rule().getMaximumValue();
        date.getMonthOfYear().maxLengthInDays();
//        DayOfMonth.RULE.getMaximumValue(date);
        FEBRUARY.maxLengthInDays();
//        DayOfMonth.RULE.getMaximumValue(february());
        
        DayOfWeek dow = MONDAY;
        dow = dow.next();
        dow = dow.plusDays(3);
//        dow = dow.plusDaysSkipping(3, SATURDAY, SUNDAY);
        
//        day = day.plusSkipping(days(3), WEDNESDAY_PM, SATURDAY, SUNDAY, FOURTH_JULY, XMAS_BREAK);
//        
//        int dayIndex = day.value();
//        int dayIndex = day.value(Territory.US);
//        int dayIndex = day.valueIndexedFrom(SUNDAY);
////        SundayBasedDayOfWeek.MONDAY != DayOfWeek.MONDAY;
//        Territory.US.dayOfWeekComparator();
//        
//        date.dayOfMonth().value();
        
//        {
//            Days days = days(3);
//            Seconds secs = days.convertTo(Seconds.class);
//            int amount = secs.getAmount();
//        }
//        {
//            Duration days = days(3);
//            int amount = days.getTotalAmount(SECOND);
//        }
//        {
//            Measure<Integer, Duration> days = days(3);
//            Measure<Integer, Duration> secs = days.to(SECOND);
//            int secs = days.intValue(SECOND);
//            secs.toString();
//        }
//        d1 = d2;
//        d2 = d1;
//        d1 = d3;
//        secondOfMinute(sec);
//        minuteOfHour(min);
        
//        year(2008).append(MonthOfYear.JULY).append(dayOfMonth(3))
//        LocalDateTime dt = date.append(tod);
        
    }

}