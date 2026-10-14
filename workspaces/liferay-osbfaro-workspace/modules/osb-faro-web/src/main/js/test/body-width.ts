export const setBodyWidth = (width: number) =>
	Object.defineProperty(document.body, 'clientWidth', {
		configurable: true,
		value: width,
	});

export const resetBodyWidth = () =>
	Reflect.deleteProperty(document.body, 'clientWidth');
