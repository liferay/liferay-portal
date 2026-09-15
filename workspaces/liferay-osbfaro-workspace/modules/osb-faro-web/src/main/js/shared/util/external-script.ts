/**
 * Appends a third party script or stylesheet to the page, with the Content
 * Security Policy nonce every one of them needs.
 */

interface LinkOptions {
	href: string;
	id?: string;
	rel: string;
}

interface ScriptOptions {
	attributes?: Record<string, string>;
	id?: string;
	innerHTML?: string;
	src?: string;
}

export function applyNonce(element: HTMLElement) {
	const nonce = (Liferay as unknown as {CSP?: {nonce?: string}}).CSP?.nonce;

	if (nonce) {
		element.setAttribute('nonce', nonce);
	}
}

export function appendLink(options: LinkOptions) {
	const link = Object.assign(document.createElement('link'), options);

	applyNonce(link);

	document.head.appendChild(link);

	return link;
}

export function appendScript({attributes = {}, ...options}: ScriptOptions) {
	const script = Object.assign(document.createElement('script'), options);

	if (options.src) {
		script.async = true;
	}

	// A loader reading its configuration back with `getAttribute` needs real
	// attributes, which assigning a property does not create for a nonstandard
	// name such as `ai-hub-url`.

	for (const [name, value] of Object.entries(attributes)) {
		script.setAttribute(name, value);
	}

	applyNonce(script);

	document.body.appendChild(script);

	return script;
}
