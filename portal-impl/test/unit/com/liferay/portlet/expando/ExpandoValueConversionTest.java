/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando;

import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.expando.service.impl.ExpandoValueLocalServiceImpl;

import java.math.BigDecimal;

import java.time.format.DateTimeParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import jodd.typeconverter.TypeConversionException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Raymond Augé
 * @author Amadea Fejes
 */
public class ExpandoValueConversionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testBoolean1() {
		Boolean convertedBoolean = _converter.convertType(
			ExpandoColumnConstants.BOOLEAN, "true");

		Assert.assertTrue(convertedBoolean);
	}

	@Test
	public void testBoolean2() {
		Boolean convertedBoolean = _converter.convertType(
			ExpandoColumnConstants.BOOLEAN, "false");

		Assert.assertFalse(convertedBoolean);
	}

	@Test(expected = TypeConversionException.class)
	public void testBoolean3() {
		_converter.convertType(ExpandoColumnConstants.BOOLEAN, "other");
	}

	@Test
	public void testBooleanArray1() {
		boolean[] convertedBooleans = _converter.convertType(
			ExpandoColumnConstants.BOOLEAN_ARRAY, "true");

		Assert.assertEquals(
			Arrays.toString(convertedBooleans), 1, convertedBooleans.length);
		Assert.assertTrue(convertedBooleans[0]);
	}

	@Test
	public void testBooleanArray2() {
		boolean[] convertedBooleans = _converter.convertType(
			ExpandoColumnConstants.BOOLEAN_ARRAY, "false,true");

		Assert.assertEquals(
			Arrays.toString(convertedBooleans), 2, convertedBooleans.length);
		Assert.assertTrue(convertedBooleans[1]);
		Assert.assertFalse(convertedBooleans[0]);
	}

	@Test(expected = TypeConversionException.class)
	public void testBooleanArray3() {
		_converter.convertType(
			ExpandoColumnConstants.BOOLEAN_ARRAY, "other,false");
	}

	@Test
	public void testBooleanArray4() {
		boolean[] convertedBooleans = _converter.convertType(
			ExpandoColumnConstants.BOOLEAN_ARRAY, "[false,true]");

		Assert.assertEquals(
			Arrays.toString(convertedBooleans), 2, convertedBooleans.length);
		Assert.assertTrue(convertedBooleans[1]);
		Assert.assertFalse(convertedBooleans[0]);
	}

	@Test(expected = TypeConversionException.class)
	public void testBooleanArray5() {
		_converter.convertType(
			ExpandoColumnConstants.BOOLEAN_ARRAY, "[other,false]");
	}

	@Test(expected = TypeConversionException.class)
	public void testBooleanArray6() {
		boolean[] convertedBooleans = _converter.convertType(
			ExpandoColumnConstants.BOOLEAN_ARRAY, "[\"false\",true]");

		Assert.assertEquals(
			Arrays.toString(convertedBooleans), 2, convertedBooleans.length);
		Assert.assertTrue(convertedBooleans[1]);
		Assert.assertFalse(convertedBooleans[0]);
	}

	@Test(expected = TypeConversionException.class)
	public void testBooleanArray7() {
		_converter.convertType(
			ExpandoColumnConstants.BOOLEAN_ARRAY, "[\"other\",false]");
	}

	@Test
	public void testBooleanArray8() {
		Collection<String> booleans = new ArrayList<>();

		booleans.add("true");
		booleans.add("false");

		boolean[] convertedBooleans = _converter.convertType(
			ExpandoColumnConstants.BOOLEAN_ARRAY, booleans);

		Assert.assertEquals(
			Arrays.toString(convertedBooleans), 2, convertedBooleans.length);
		Assert.assertTrue(convertedBooleans[0]);
		Assert.assertFalse(convertedBooleans[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testBooleanArray9() {
		Collection<String> booleans = new ArrayList<>();

		booleans.add("true");
		booleans.add("other");

		_converter.convertType(ExpandoColumnConstants.BOOLEAN_ARRAY, booleans);
	}

	@Test
	public void testDate1() {
		long time = System.currentTimeMillis();

		Date convertedDate = _converter.convertType(
			ExpandoColumnConstants.DATE, time);

		Assert.assertEquals(time, convertedDate.getTime());
	}

	@Test(expected = DateTimeParseException.class)
	public void testDate2() {
		_converter.convertType(ExpandoColumnConstants.DATE, "other");
	}

	@Test
	public void testDate3() {
		Date convertedDate = _converter.convertType(
			ExpandoColumnConstants.DATE, "2021-05-09");

		Assert.assertEquals(
			"Sun May 09 00:00:00 GMT 2021", convertedDate.toString());

		convertedDate = _converter.convertType(
			ExpandoColumnConstants.DATE, "2021-5-9");

		Assert.assertEquals(
			"Sun May 09 00:00:00 GMT 2021", convertedDate.toString());
	}

	@Test
	public void testDateArray1() {
		long time1 = System.currentTimeMillis();
		long time2 = System.currentTimeMillis();

		Date[] convertedDates = _converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY,
			new String[] {String.valueOf(time1), String.valueOf(time2)});

		Assert.assertEquals(
			Arrays.toString(convertedDates), 2, convertedDates.length);
		Assert.assertEquals(time1, convertedDates[0].getTime());
		Assert.assertEquals(time2, convertedDates[1].getTime());
	}

	@Test
	public void testDateArray2() {
		long time = 1376510136750L;

		Date[] convertedDates = _converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY, String.valueOf(time));

		Assert.assertEquals(
			Arrays.toString(convertedDates), 1, convertedDates.length);
		Assert.assertEquals(time, convertedDates[0].getTime());
	}

	@Test
	public void testDateArray3() {
		long time1 = 1376510136750L;
		long time2 = 1376510136751L;

		Date[] convertedDates = _converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY,
			String.valueOf(time1) + ", " + String.valueOf(time2));

		Assert.assertEquals(
			Arrays.toString(convertedDates), 2, convertedDates.length);
		Assert.assertEquals(time1, convertedDates[0].getTime());
		Assert.assertEquals(time2, convertedDates[1].getTime());
	}

	@Test
	public void testDateArray4() {
		long time1 = 1376510136750L;
		long time2 = 1376510136751L;

		Date[] convertedDates = _converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY,
			StringBundler.concat("[", time1, ", ", time2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedDates), 2, convertedDates.length);
		Assert.assertEquals(time1, convertedDates[0].getTime());
		Assert.assertEquals(time2, convertedDates[1].getTime());
	}

	@Test(expected = DateTimeParseException.class)
	public void testDateArray5() {
		_converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY, "1376510136750, other");
	}

	@Test(expected = DateTimeParseException.class)
	public void testDateArray6() {
		_converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY, "[1376510136750, other]");
	}

	@Test(expected = DateTimeParseException.class)
	public void testDateArray7() {
		_converter.convertType(ExpandoColumnConstants.DATE_ARRAY, "other");
	}

	@Test
	public void testDateArray8() {
		long time = 1376510136750L;

		Date[] convertedDates = _converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY, time);

		Assert.assertEquals(
			Arrays.toString(convertedDates), 1, convertedDates.length);
		Assert.assertEquals(time, convertedDates[0].getTime());
	}

	@Test
	public void testDateArray9() {
		long[] times = {1376510136750L, 1376510136560L};

		Date[] convertedDates = _converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY, times);

		Assert.assertEquals(
			Arrays.toString(convertedDates), 2, convertedDates.length);
		Assert.assertEquals(times[0], convertedDates[0].getTime());
		Assert.assertEquals(times[1], convertedDates[1].getTime());
	}

	@Test(expected = ClassCastException.class)
	public void testDateArray10() {
		_converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY,
			new int[] {1376510136, 1376510136});
	}

	@Test
	public void testDateArray11() {
		Date[] convertedDates = _converter.convertType(
			ExpandoColumnConstants.DATE_ARRAY,
			new String[] {"2021-05-09", "2021-5-9"});

		Assert.assertEquals(
			Arrays.toString(convertedDates), 2, convertedDates.length);
		Assert.assertEquals(
			"Sun May 09 00:00:00 GMT 2021", convertedDates[0].toString());
		Assert.assertEquals(
			"Sun May 09 00:00:00 GMT 2021", convertedDates[1].toString());
	}

	@Test
	public void testDouble1() {
		Double negativeDouble = -456.23;

		Double convertedDouble = _converter.convertType(
			ExpandoColumnConstants.DOUBLE, negativeDouble.toString());

		Assert.assertEquals(negativeDouble, convertedDouble);
	}

	@Test
	public void testDouble2() {
		Double positiveDouble = 345.4;

		Double convertedDouble = _converter.convertType(
			ExpandoColumnConstants.DOUBLE, positiveDouble.toString());

		Assert.assertEquals(positiveDouble, convertedDouble);
	}

	@Test(expected = TypeConversionException.class)
	public void testDouble3() {
		_converter.convertType(ExpandoColumnConstants.DOUBLE, "other");
	}

	@Test
	public void testDoubleArray1() {
		_converter.convertType(ExpandoColumnConstants.DOUBLE_ARRAY, "13.4");
	}

	@Test
	public void testDoubleArray2() {
		double double1 = 345.4;
		int double2 = 56;

		double[] convertedDoubles = _converter.convertType(
			ExpandoColumnConstants.DOUBLE_ARRAY, double1 + ", " + double2);

		Assert.assertEquals(
			Arrays.toString(convertedDoubles), 2, convertedDoubles.length);
		Assert.assertEquals(double1, convertedDoubles[0], 0);
		Assert.assertEquals(double2, convertedDoubles[1], 0);
	}

	@Test(expected = TypeConversionException.class)
	public void testDoubleArray3() {
		_converter.convertType(
			ExpandoColumnConstants.DOUBLE_ARRAY, "other,23.4");
	}

	@Test
	public void testDoubleArray4() {
		double double1 = 56.6567;
		double double2 = 0.0000345;

		double[] convertedDoubles = _converter.convertType(
			ExpandoColumnConstants.DOUBLE_ARRAY,
			StringBundler.concat("[", double1, ", ", double2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedDoubles), 2, convertedDoubles.length);
		Assert.assertEquals(double1, convertedDoubles[0], 0);
		Assert.assertEquals(double2, convertedDoubles[1], 0);
	}

	@Test(expected = TypeConversionException.class)
	public void testDoubleArray5() {
		_converter.convertType(
			ExpandoColumnConstants.DOUBLE_ARRAY, "[0.34,other]");
	}

	@Test(expected = TypeConversionException.class)
	public void testDoubleArray6() {
		double double1 = 34.67;
		double double2 = 12.45;

		double[] convertedDoubles = _converter.convertType(
			ExpandoColumnConstants.DOUBLE_ARRAY,
			StringBundler.concat("[\"", double1, "\",", double2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedDoubles), 2, convertedDoubles.length);
		Assert.assertEquals(double1, convertedDoubles[0], 0);
		Assert.assertEquals(double2, convertedDoubles[1], 0);
	}

	@Test(expected = TypeConversionException.class)
	public void testDoubleArray7() {
		_converter.convertType(
			ExpandoColumnConstants.DOUBLE_ARRAY, "[\"other\",34.65]");
	}

	@Test
	public void testDoubleArray8() {
		Collection<String> doubles = new ArrayList<>();

		doubles.add(String.valueOf(Double.MAX_VALUE));
		doubles.add(String.valueOf(Integer.MAX_VALUE));

		double[] convertedDoubles = _converter.convertType(
			ExpandoColumnConstants.DOUBLE_ARRAY, doubles);

		Assert.assertEquals(
			Arrays.toString(convertedDoubles), 2, convertedDoubles.length);
		Assert.assertEquals(Double.MAX_VALUE, convertedDoubles[0], 0);
		Assert.assertEquals(Integer.MAX_VALUE, convertedDoubles[1], 0);
	}

	@Test(expected = TypeConversionException.class)
	public void testDoubleArray9() {
		Collection<String> booleans = new ArrayList<>();

		booleans.add("12.5");
		booleans.add("other");

		_converter.convertType(ExpandoColumnConstants.DOUBLE_ARRAY, booleans);
	}

	@Test
	public void testFloat1() {
		Float negativeFloat = -456.23F;

		Float convertedFloat = _converter.convertType(
			ExpandoColumnConstants.FLOAT, negativeFloat.toString());

		Assert.assertEquals(negativeFloat, convertedFloat);
	}

	@Test
	public void testFloat2() {
		Float positiveFloat = 345.4F;

		Float convertedFloat = _converter.convertType(
			ExpandoColumnConstants.FLOAT, positiveFloat.toString());

		Assert.assertEquals(positiveFloat, convertedFloat);
	}

	@Test(expected = TypeConversionException.class)
	public void testFloat3() {
		_converter.convertType(ExpandoColumnConstants.FLOAT, "other");
	}

	@Test
	public void testFloatArray1() {
		Float floatValue = 13.4F;

		float[] convertedFloats = _converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY, floatValue.toString());

		Assert.assertEquals(
			Arrays.toString(convertedFloats), 1, convertedFloats.length);
		Assert.assertEquals(floatValue, convertedFloats[0], 0);
	}

	@Test
	public void testFloatArray2() {
		float float1 = 345.67F;
		int float2 = 56;

		float[] convertedFloats = _converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY, float1 + ", " + float2);

		Assert.assertEquals(
			Arrays.toString(convertedFloats), 2, convertedFloats.length);
		Assert.assertEquals(float1, convertedFloats[0], 0);
		Assert.assertEquals(float2, convertedFloats[1], 0);
	}

	@Test(expected = TypeConversionException.class)
	public void testFloatArray3() {
		_converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY, "other,23.4");
	}

	@Test
	public void testFloatArray4() {
		float float1 = 56.6567F;
		float float2 = 0.0000345F;

		float[] convertedFloats = _converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY,
			StringBundler.concat("[", float1, ", ", float2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedFloats), 2, convertedFloats.length);
		Assert.assertEquals(float1, convertedFloats[0], 0);
		Assert.assertEquals(float2, convertedFloats[1], 0);
	}

	@Test(expected = TypeConversionException.class)
	public void testFloatArray5() {
		_converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY, "[0.34,other]");
	}

	@Test(expected = TypeConversionException.class)
	public void testFloatArray6() {
		_converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY, "[\"34.67f\",12.45f]");
	}

	@Test(expected = TypeConversionException.class)
	public void testFloatArray7() {
		_converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY, "[\"other\",34.65]");
	}

	@Test
	public void testFloatArray8() {
		Collection<String> floats = new ArrayList<>();

		floats.add(String.valueOf(Float.MAX_VALUE));
		floats.add(String.valueOf(Integer.MAX_VALUE));

		float[] convertedFloats = _converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY, floats);

		Assert.assertEquals(
			Arrays.toString(convertedFloats), 2, convertedFloats.length);
		Assert.assertEquals(Float.MAX_VALUE, convertedFloats[0], 0);
		Assert.assertEquals(Integer.MAX_VALUE, convertedFloats[1], 0);
	}

	@Test
	public void testFloatArray9() {
		Collection<String> floats = new ArrayList<>();

		floats.add(String.valueOf(Double.MAX_VALUE));
		floats.add(String.valueOf(Integer.MAX_VALUE));

		float[] convertedFloats = _converter.convertType(
			ExpandoColumnConstants.FLOAT_ARRAY, floats);

		Assert.assertEquals(
			Arrays.toString(convertedFloats), 2, convertedFloats.length);
		Assert.assertEquals(Float.POSITIVE_INFINITY, convertedFloats[0], 0);
		Assert.assertEquals(Integer.MAX_VALUE, convertedFloats[1], 0);
	}

	@Test
	public void testInteger1() {
		Integer positiveInteger = 456;

		Integer convertedInteger = _converter.convertType(
			ExpandoColumnConstants.INTEGER, positiveInteger.toString());

		Assert.assertEquals(positiveInteger, convertedInteger);
	}

	@Test
	public void testInteger2() {
		Integer negativeInteger = -345;

		Integer convertedInteger = _converter.convertType(
			ExpandoColumnConstants.INTEGER, negativeInteger.toString());

		Assert.assertEquals(negativeInteger, convertedInteger);
	}

	@Test(expected = TypeConversionException.class)
	public void testInteger3() {
		_converter.convertType(ExpandoColumnConstants.INTEGER, "13.6");
	}

	@Test
	public void testIntegerArray1() {
		Integer integer = 13;

		int[] convertedIntegers = _converter.convertType(
			ExpandoColumnConstants.INTEGER_ARRAY, integer.toString());

		Assert.assertEquals(
			Arrays.toString(convertedIntegers), 1, convertedIntegers.length);
		Assert.assertEquals(integer.intValue(), convertedIntegers[0]);
	}

	@Test
	public void testIntegerArray2() {
		Integer integer1 = 345;
		Integer integer2 = 56;

		int[] convertedIntegers = _converter.convertType(
			ExpandoColumnConstants.INTEGER_ARRAY, integer1 + ", " + integer2);

		Assert.assertEquals(
			Arrays.toString(convertedIntegers), 2, convertedIntegers.length);
		Assert.assertEquals(integer1.intValue(), convertedIntegers[0]);
		Assert.assertEquals(integer2.intValue(), convertedIntegers[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testIntegerArray3() {
		_converter.convertType(
			ExpandoColumnConstants.INTEGER_ARRAY, "675,23.4");
	}

	@Test
	public void testIntegerArray4() {
		Integer integer1 = 56;
		Integer integer2 = 1;

		int[] convertedIntegers = _converter.convertType(
			ExpandoColumnConstants.INTEGER_ARRAY,
			StringBundler.concat("[", integer1, ", ", integer2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedIntegers), 2, convertedIntegers.length);
		Assert.assertEquals(integer1.intValue(), convertedIntegers[0]);
		Assert.assertEquals(integer2.intValue(), convertedIntegers[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testIntegerArray5() {
		_converter.convertType(
			ExpandoColumnConstants.INTEGER_ARRAY, "[0,56.23]");
	}

	@Test(expected = TypeConversionException.class)
	public void testIntegerArray6() {
		_converter.convertType(
			ExpandoColumnConstants.INTEGER_ARRAY, "[\"34\",12]");
	}

	@Test(expected = TypeConversionException.class)
	public void testIntegerArray7() {
		_converter.convertType(
			ExpandoColumnConstants.INTEGER_ARRAY, "[\"34.5\",34]");
	}

	@Test
	public void testIntegerArray8() {
		Collection<String> integers = new ArrayList<>();

		integers.add(String.valueOf(Integer.MIN_VALUE));
		integers.add(String.valueOf(Integer.MAX_VALUE));

		int[] convertedIntegers = _converter.convertType(
			ExpandoColumnConstants.INTEGER_ARRAY, integers);

		Assert.assertEquals(
			Arrays.toString(convertedIntegers), 2, convertedIntegers.length);
		Assert.assertEquals(Integer.MIN_VALUE, convertedIntegers[0]);
		Assert.assertEquals(Integer.MAX_VALUE, convertedIntegers[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testIntegerArray9() {
		Collection<String> integers = new ArrayList<>();

		integers.add(String.valueOf(Double.MAX_VALUE));
		integers.add(String.valueOf(Integer.MAX_VALUE));

		_converter.convertType(ExpandoColumnConstants.INTEGER_ARRAY, integers);
	}

	@Test
	public void testLong1() {
		Long positiveLong = 456L;

		Long convertedLong = _converter.convertType(
			ExpandoColumnConstants.LONG, positiveLong.toString());

		Assert.assertEquals(positiveLong, convertedLong);
	}

	@Test
	public void testLong2() {
		Long negativeLong = -345L;

		Long convertedLong = _converter.convertType(
			ExpandoColumnConstants.LONG, negativeLong.toString());

		Assert.assertEquals(negativeLong, convertedLong);
	}

	@Test(expected = TypeConversionException.class)
	public void testLong3() {
		_converter.convertType(ExpandoColumnConstants.LONG, "13.4");
	}

	@Test
	public void testLongArray1() {
		Long longValue = 13L;

		long[] convertedLongs = _converter.convertType(
			ExpandoColumnConstants.LONG_ARRAY, longValue.toString());

		Assert.assertEquals(
			Arrays.toString(convertedLongs), 1, convertedLongs.length);
		Assert.assertEquals(longValue.longValue(), convertedLongs[0]);
	}

	@Test
	public void testLongArray2() {
		Long long1 = 345L;
		Long long2 = 56L;

		long[] convertedLongs = _converter.convertType(
			ExpandoColumnConstants.LONG_ARRAY,
			long1.longValue() + ", " + long2.longValue());

		Assert.assertEquals(
			Arrays.toString(convertedLongs), 2, convertedLongs.length);
		Assert.assertEquals(long1.longValue(), convertedLongs[0]);
		Assert.assertEquals(long2.longValue(), convertedLongs[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testLongArray3() {
		_converter.convertType(ExpandoColumnConstants.LONG_ARRAY, "675,23.4");
	}

	@Test
	public void testLongArray4() {
		Long long1 = 56L;
		Long long2 = 1L;

		long[] convertedLongs = _converter.convertType(
			ExpandoColumnConstants.LONG_ARRAY,
			StringBundler.concat("[", long1, ", ", long2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedLongs), 2, convertedLongs.length);
		Assert.assertEquals(long1.longValue(), convertedLongs[0]);
		Assert.assertEquals(long2.longValue(), convertedLongs[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testLongArray5() {
		_converter.convertType(ExpandoColumnConstants.LONG_ARRAY, "[0,56.23]");
	}

	@Test(expected = TypeConversionException.class)
	public void testLongArray6() {
		_converter.convertType(
			ExpandoColumnConstants.LONG_ARRAY, "[\"34\",12]");
	}

	@Test(expected = TypeConversionException.class)
	public void testLongArray7() {
		_converter.convertType(
			ExpandoColumnConstants.LONG_ARRAY, "[\"34.5\",34]");
	}

	@Test
	public void testLongArray8() {
		Collection<String> longs = new ArrayList<>();

		longs.add(String.valueOf(Long.MIN_VALUE));
		longs.add(String.valueOf(Long.MAX_VALUE));

		long[] convertedLongs = _converter.convertType(
			ExpandoColumnConstants.LONG_ARRAY, longs);

		Assert.assertEquals(
			Arrays.toString(convertedLongs), 2, convertedLongs.length);
		Assert.assertEquals(Long.MIN_VALUE, convertedLongs[0]);
		Assert.assertEquals(Long.MAX_VALUE, convertedLongs[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testLongArray9() {
		Collection<String> longs = new ArrayList<>();

		longs.add(String.valueOf(Double.MAX_VALUE));
		longs.add(String.valueOf(Long.MAX_VALUE));

		_converter.convertType(ExpandoColumnConstants.LONG_ARRAY, longs);
	}

	@Test
	public void testNumber1() {
		Number positiveNumber = 456;

		Number convertedNumber = _converter.convertType(
			ExpandoColumnConstants.NUMBER, positiveNumber.toString());

		Assert.assertEquals(
			positiveNumber.intValue(), convertedNumber.intValue());
	}

	@Test
	public void testNumber2() {
		Number negativeNumber = -345;

		Number convertedNumber = _converter.convertType(
			ExpandoColumnConstants.NUMBER, negativeNumber);

		Assert.assertEquals(
			negativeNumber.intValue(), convertedNumber.intValue());
	}

	@Test(expected = TypeConversionException.class)
	public void testNumber3() {
		_converter.convertType(ExpandoColumnConstants.NUMBER, "other");
	}

	@Test
	public void testNumberArray1() {
		Number positiveNumber = 13;

		Number[] convertedNumber = _converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY, positiveNumber.toString());

		Assert.assertEquals(
			Arrays.toString(convertedNumber), 1, convertedNumber.length);
		Assert.assertEquals(
			new BigDecimal(positiveNumber.intValue()), convertedNumber[0]);
	}

	@Test
	public void testNumberArray2() {
		Number number1 = 345;
		Number number2 = 56;

		Number[] convertedNumber = _converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY, number1 + ", " + number2);

		Assert.assertEquals(
			Arrays.toString(convertedNumber), 2, convertedNumber.length);
		Assert.assertEquals(
			new BigDecimal(number1.intValue()), convertedNumber[0]);
		Assert.assertEquals(
			new BigDecimal(number2.intValue()), convertedNumber[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testNumberArray3() {
		_converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY, "675.345,other");
	}

	@Test
	public void testNumberArray4() {
		Number number1 = 56;
		Number number2 = 1;

		Number[] convertedNumber = _converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY,
			StringBundler.concat("[", number1, ", ", number2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedNumber), 2, convertedNumber.length);
		Assert.assertEquals(
			new BigDecimal(number1.intValue()), convertedNumber[0]);
		Assert.assertEquals(
			new BigDecimal(number2.intValue()), convertedNumber[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testNumberArray5() {
		_converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY, "[0,other]");
	}

	@Test
	public void testNumberArray6() {
		Number number1 = 34;
		Number number2 = 12;

		Number[] convertedNumber = _converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY,
			StringBundler.concat("[\"", number1, "\", ", number2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedNumber), 2, convertedNumber.length);
		Assert.assertEquals(
			new BigDecimal(number1.intValue()), convertedNumber[0]);
		Assert.assertEquals(
			new BigDecimal(number2.intValue()), convertedNumber[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testNumberArray7() {
		_converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY, "[\"other\",34]");
	}

	@Test
	public void testNumberArray8() {
		Collection<String> numbers = new ArrayList<>();

		numbers.add(String.valueOf(Double.MIN_VALUE));
		numbers.add(String.valueOf(Double.MAX_VALUE));

		Number[] convertedNumber = _converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY, numbers);

		Assert.assertEquals(
			Arrays.toString(convertedNumber), 2, convertedNumber.length);
		Assert.assertEquals(
			Double.MIN_VALUE, convertedNumber[0].doubleValue(), 0);
		Assert.assertEquals(
			Double.MAX_VALUE, convertedNumber[1].doubleValue(), 0);
	}

	@Test
	public void testNumberArray9() {
		Collection<String> numbers = new ArrayList<>();

		numbers.add(String.valueOf(Double.MAX_VALUE));
		numbers.add(String.valueOf(Long.MAX_VALUE));

		Number[] convertedNumber = _converter.convertType(
			ExpandoColumnConstants.NUMBER_ARRAY, numbers);

		Assert.assertEquals(
			Arrays.toString(convertedNumber), 2, convertedNumber.length);
		Assert.assertEquals(
			Double.MAX_VALUE, convertedNumber[0].doubleValue(), 0);
		Assert.assertEquals(
			Long.MAX_VALUE, convertedNumber[1].doubleValue(), 0);
	}

	@Test
	public void testShort1() {
		Short positiveShort = 456;

		Short convertedShort = _converter.convertType(
			ExpandoColumnConstants.SHORT, positiveShort.toString());

		Assert.assertEquals(positiveShort, convertedShort);
	}

	@Test
	public void testShort2() {
		Short negativeShort = -345;

		Short convertedShort = _converter.convertType(
			ExpandoColumnConstants.SHORT, negativeShort.toString());

		Assert.assertEquals(negativeShort, convertedShort);
	}

	@Test(expected = TypeConversionException.class)
	public void testShort3() {
		_converter.convertType(ExpandoColumnConstants.SHORT, "12344535");
	}

	@Test
	public void testShortArray1() {
		Short positiveShort = 13;

		short[] convertedShorts = _converter.convertType(
			ExpandoColumnConstants.SHORT_ARRAY, positiveShort.toString());

		Assert.assertEquals(
			Arrays.toString(convertedShorts), 1, convertedShorts.length);
		Assert.assertEquals(positiveShort.shortValue(), convertedShorts[0]);
	}

	@Test
	public void testShortArray2() {
		Short short1 = 345;
		Short short2 = 56;

		short[] convertedShorts = _converter.convertType(
			ExpandoColumnConstants.SHORT_ARRAY, short1 + ", " + short2);

		Assert.assertEquals(
			Arrays.toString(convertedShorts), 2, convertedShorts.length);
		Assert.assertEquals(short1.shortValue(), convertedShorts[0]);
		Assert.assertEquals(short2.shortValue(), convertedShorts[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testShortArray3() {
		_converter.convertType(
			ExpandoColumnConstants.SHORT_ARRAY, "675,12344535");
	}

	@Test
	public void testShortArray4() {
		Short short1 = 56;
		Short short2 = 1;

		short[] convertedShorts = _converter.convertType(
			ExpandoColumnConstants.SHORT_ARRAY,
			StringBundler.concat("[", short1, ", ", short2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedShorts), 2, convertedShorts.length);
		Assert.assertEquals(short1.shortValue(), convertedShorts[0]);
		Assert.assertEquals(short2.shortValue(), convertedShorts[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testShortArray5() {
		_converter.convertType(
			ExpandoColumnConstants.SHORT_ARRAY, "[0,12344535]");
	}

	@Test(expected = TypeConversionException.class)
	public void testShortArray6() {
		Short short1 = 34;
		Short short2 = 12;

		short[] convertedShorts = _converter.convertType(
			ExpandoColumnConstants.SHORT_ARRAY,
			StringBundler.concat("[\"", short1, "\", ", short2, "]"));

		Assert.assertEquals(
			Arrays.toString(convertedShorts), 2, convertedShorts.length);
		Assert.assertEquals(short1.shortValue(), convertedShorts[0]);
		Assert.assertEquals(short2.shortValue(), convertedShorts[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testShortArray7() {
		_converter.convertType(
			ExpandoColumnConstants.SHORT_ARRAY, "[\"12344535\",34]");
	}

	@Test
	public void testShortArray8() {
		Collection<String> shorts = new ArrayList<>();

		shorts.add(String.valueOf(Short.MIN_VALUE));
		shorts.add(String.valueOf(Short.MAX_VALUE));

		short[] convertedShorts = _converter.convertType(
			ExpandoColumnConstants.SHORT_ARRAY, shorts);

		Assert.assertEquals(
			Arrays.toString(convertedShorts), 2, convertedShorts.length);
		Assert.assertEquals(Short.MIN_VALUE, convertedShorts[0]);
		Assert.assertEquals(Short.MAX_VALUE, convertedShorts[1]);
	}

	@Test(expected = TypeConversionException.class)
	public void testShortArray9() {
		Collection<String> shorts = new ArrayList<>();

		shorts.add(String.valueOf(Double.MAX_VALUE));
		shorts.add(String.valueOf(Short.MAX_VALUE));

		_converter.convertType(ExpandoColumnConstants.SHORT_ARRAY, shorts);
	}

	@Test
	public void testString1() {
		String string = "[randomtext]";

		String convertedString = (String)_converter.convertType(
			ExpandoColumnConstants.STRING, string);

		Assert.assertEquals(string, convertedString);
	}

	@Test
	public void testString2() {
		Integer integer = 16;

		String convertedString = (String)_converter.convertType(
			ExpandoColumnConstants.STRING, integer.toString());

		Assert.assertEquals(integer.toString(), convertedString);
	}

	@Test
	public void testStringArray1() {
		String string = "randomtext";

		String[] convertedArray = (String[])_converter.convertType(
			ExpandoColumnConstants.STRING_ARRAY, "[randomtext]");

		Assert.assertEquals(
			Arrays.toString(convertedArray), 1, convertedArray.length);
		Assert.assertEquals(string, convertedArray[0]);
	}

	@Test
	public void testStringArray2() {
		String string = "randomtext";

		String[] convertedArray = (String[])_converter.convertType(
			ExpandoColumnConstants.STRING_ARRAY, "[randomtext,randomtext]");

		Assert.assertEquals(
			Arrays.toString(convertedArray), 2, convertedArray.length);
		Assert.assertEquals(string, convertedArray[0]);
		Assert.assertEquals(string, convertedArray[1]);
	}

	@Test
	public void testStringArray3() {
		String string = "randomtext";

		String[] convertedArray = (String[])_converter.convertType(
			ExpandoColumnConstants.STRING_ARRAY, "randomtext,randomtext");

		Assert.assertEquals(
			Arrays.toString(convertedArray), 2, convertedArray.length);
		Assert.assertEquals(string, convertedArray[0]);
		Assert.assertEquals(string, convertedArray[1]);
	}

	@Test
	public void testStringArray4() {
		Collection<String> integers = new ArrayList<>();

		integers.add(String.valueOf(16));
		integers.add(String.valueOf(2016));

		String[] convertedArray = (String[])_converter.convertType(
			ExpandoColumnConstants.STRING_ARRAY, integers);

		Assert.assertEquals(
			Arrays.toString(convertedArray), 2, convertedArray.length);
		Assert.assertEquals(String.valueOf(16), convertedArray[0]);
		Assert.assertEquals(String.valueOf(2016), convertedArray[1]);
	}

	private final Converter _converter = new Converter();

	private static class Converter extends ExpandoValueLocalServiceImpl {

		@Override
		public <T> T convertType(int type, Object data) {
			return super.convertType(type, data);
		}

	}

}