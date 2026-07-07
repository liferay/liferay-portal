import {formatStringToLowercase} from 'shared/util/util';
import {formatTime, getMillisecondsFromTime} from 'shared/util/time';
import {isArray, isNil, isObject, isString} from 'lodash';
import {sub} from 'shared/util/lang';

/**
 * Wraps a validator result with a Promise.
 *
 * Formik v2 expects field validators to resolve with the error message
 * (or a falsy value if valid) rather than reject, so this helper always
 * resolves. When passed a Promise, it is returned unchanged.
 */

export function toPromise<T>(value: Promise<T> | T): Promise<T> {
	if (value instanceof Promise) {
		return value;
	}

	return Promise.resolve(value);
}

export function validateInputMessage(messageValue: string) {
	return (value: string) => {
		let error = '';

		const invalid =
			formatStringToLowercase(value) !==
			formatStringToLowercase(messageValue);

		if (invalid) {
			error = Liferay.Language.get('string-does-not-match');
		}

		return error;
	};
}

export function validateDateRangeRequired({
	end,
	start,
}: {
	end: string;
	start: string;
}) {
	let error = '';

	if (!end || !start) {
		error = Liferay.Language.get('required');
	}

	return toPromise(error);
}

export const validateGreaterThanZero = (value: string) => {
	let error = '';

	if (Number(value) <= 0) {
		error = sub(Liferay.Language.get('must-be-greater-than-x'), [
			'0',
		]) as string;
	}

	return toPromise(error);
};

export const validateIsInteger = (value: string) => {
	let error = '';

	if (!Number.isInteger(Number(value))) {
		error = Liferay.Language.get('must-be-an-integer');
	}

	return toPromise(error);
};

export function validateRequired(
	value: {value: any} | string | Array<string>,
	errorMessage?: string
) {
	let error = '';

	if (
		isNil(value) ||
		(isString(value) && !value.trim()) ||
		(isArray(value) && !value.length) ||
		(!isArray(value) && isObject(value) && !value.value)
	) {
		error = errorMessage || Liferay.Language.get('required');
	}

	return toPromise(error);
}

export function validateMinDuration(minDuration: string) {
	return (value: string) => {
		let error = '';

		const minDurationInMilliseconds: number = getMillisecondsFromTime(
			minDuration.replace(/_/g, '0')
		);

		const valueInMilliseconds: number = getMillisecondsFromTime(
			value.replace(/_/g, '0')
		);

		if (valueInMilliseconds < minDurationInMilliseconds) {
			error = sub(Liferay.Language.get('must-be-greater-than-x'), [
				formatTime(minDurationInMilliseconds - 1000),
			]) as string;
		}

		return toPromise(error);
	};
}

export function validateMinLength(minLength: number) {
	return (value: string) => {
		let error = '';

		if (value.length && value.length < minLength) {
			error = Liferay.Language.get(
				'does-not-meet-minimum-length-required'
			);
		}

		return toPromise(error);
	};
}

export function validateMinValue(minValue: number) {
	return (value: string) => {
		let error = '';

		if (Number(value) < minValue) {
			error = sub(Liferay.Language.get('must-be-greater-than-x'), [
				minValue - 1,
			]) as string;
		}

		return toPromise(error);
	};
}

export function validateMaxLength(maxLength: number) {
	return (value: string) => {
		let error = '';

		if (value.length > maxLength) {
			error = Liferay.Language.get('exceeds-maximum-length');
		}

		return toPromise(error);
	};
}

export function validatePattern(
	regex: RegExp,
	errorMessage: string
): (value: any) => Promise<any> {
	return (value: string) => {
		let error = '';

		if (value.length && !regex.test(value)) {
			error = errorMessage;
		}

		return toPromise(error);
	};
}

export const validateMarketoDomain = validatePattern(
	/^https:\/\/.*$/,
	Liferay.Language.get('please-enter-a-valid-marketo-url')
);

export const validateProtocol = validatePattern(
	/^(http[s]?:\/\/)/i,
	Liferay.Language.get(
		'your-url-is-missing-the-protocol.-please-include-http-or-https'
	)
);

export const validateSalesforceDomain = validatePattern(
	/^https:\/\/.*$/,
	Liferay.Language.get('please-enter-a-valid-salesforce-url')
);

export const composeValidators =
	(...validators: Array<(value: any) => Promise<string> | string>) =>
	async (value: any) => {
		for (const validator of validators) {
			const error = await validator(value);

			if (error) {
				return error;
			}
		}

		return '';
	};

export const validateExternalReferenceCode = composeValidators(
	validateRequired,
	validatePattern(
		/^[a-z0-9_-]+$/,
		Liferay.Language.get(
			'erc-must-contain-only-lowercase-letters-numbers-hyphens-and-underscores'
		)
	)
);
