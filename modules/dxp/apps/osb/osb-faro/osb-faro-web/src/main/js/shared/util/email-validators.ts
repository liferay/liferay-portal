import {toPromise} from 'shared/components/form';

const VALIDATE_DOMAINS = /^([a-zA-Z0-9_]([a-zA-Z0-9_-]{0,61}[a-zA-Z0-9_])?\.){1,126}[a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z]$/;
const VALIDATE_EMAILS = /^(?![<>{}[\]()])[^@\s]+@[^\s@]+\.[^\s@]+(?<![<>{}[\]()])$/;

export const validateEmailDomain = (emailDomain: string): boolean =>
	VALIDATE_DOMAINS.test(emailDomain);

export const validateEmailDomainArr = (
	items: string[],
	inputListValue: string
): string | void => {
	const emailDomains = items.concat(inputListValue || []);

	if (emailDomains.some(emailDomain => !validateEmailDomain(emailDomain))) {
		return Liferay.Language.get(
			'please-enter-the-domain-in-this-format-domain-com'
		);
	}
};

export const validateEmail = email => VALIDATE_EMAILS.test(email);

export const validateEmailArr = (
	items: string[],
	inputListValue: string
): Promise<string> => {
	const emails = items.concat(inputListValue || []);

	let error = '';

	if (emails.some(email => !validateEmail(email))) {
		error = Liferay.Language.get(
			'please-enter-the-email-in-this-format-sample-email-com'
		);
	}

	return toPromise(error);
};
