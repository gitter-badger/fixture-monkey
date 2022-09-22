package com.navercorp.fixturemonkey.test;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import com.navercorp.fixturemonkey.test.FixtureMonkeyV04TestSpecs.SimpleEnum;
import com.navercorp.fixturemonkey.test.FixtureMonkeyV04TestSpecs.SimpleObject;

public class IntrospectorGetterTestSpecs {

	public static class StringClass {
		private String str;
	}

	public static class StringGetter {
		private String str;

		String getStr() {
			return str;
		}
	}

	public static class StringSetter {
		private String str;

		void setStr() {
			this.str = "string";
		}
	}

	public static class StringGetterSetter {
		private String str;

		String getStr() {
			return str;
		}

		void setStr() {
			this.str = "string";
		}
	}

	@Getter
	public static class StringGetterAnnotation {
		private String str;
	}

	@Setter
	public static class StringSetterAnnotation {
		private String str;
	}

	@Getter
	@Setter
	public static class StringGetterSetterAnnotation {
		private String str;
	}
}
