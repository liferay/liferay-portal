declare namespace Liferay {
	namespace Util {
		const Cookie: {
			TYPES: {
				FUNCTIONAL: string;
				NECESSARY: string;
				PERFORMANCE: string;
				PERSONALIZATION: string;
			};
			get(name: string, type: string): string | undefined;
			remove(name: string): void;
			set(
				name: string,
				value: string,
				type: string,
				options?: {[key: string]: boolean | number | string}
			): boolean;
		};
	}
}
