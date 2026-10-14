import {useEffect} from 'react';

const restoreStyle = (
	style: CSSStyleDeclaration,
	property: string,
	value: string
) => {
	if (value) {
		style.setProperty(property, value);
	}
	else {
		style.removeProperty(property);
	}
};

export const useBodyScrollLock = (locked: boolean) => {
	useEffect(() => {
		if (!locked) {
			return;
		}

		const {body, documentElement} = document;

		const viewportWidth = documentElement.clientWidth;

		const scrollbarWidth = viewportWidth
			? window.innerWidth - viewportWidth
			: 0;

		const previousOverflow = documentElement.style.overflow;

		const previousPaddingRight = body.style.paddingRight;

		documentElement.style.overflow = 'hidden';

		if (scrollbarWidth > 0) {
			body.style.paddingRight = `${scrollbarWidth}px`;
		}

		return () => {
			restoreStyle(documentElement.style, 'overflow', previousOverflow);

			restoreStyle(body.style, 'padding-right', previousPaddingRight);
		};
	}, [locked]);
};
