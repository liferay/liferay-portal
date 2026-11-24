import OAuthReceive from '../OAuthReceive';
import React from 'react';
import {emitAuthCode, emitError, emitToken} from 'shared/util/oauth';
import {render} from '@testing-library/react';

jest.mock('shared/util/oauth', () => ({
	emitAuthCode: jest.fn(),
	emitError: jest.fn(),
	emitToken: jest.fn()
}));

describe('OAuthReceive', () => {
	afterEach(() => {
		emitToken.mockReset();
	});

	it('should render', () => {
		const {container} = render(<OAuthReceive />);

		expect(container).toMatchSnapshot();
	});

	it('should call emitToken if one was passed', () => {
		expect(emitToken).not.toBeCalled();
		const token = 'foobarbazbiz';
		const verifier = 'bizfizzbuzz';

		render(<OAuthReceive oauth_token={token} oauth_verifier={verifier} />);
		expect(emitToken).toBeCalledWith({token, verifier});
	});

	it('should call emitAuthCode if a code was passed', () => {
		expect(emitAuthCode).not.toBeCalled();
		const code = 'foobarbazbiz';

		render(<OAuthReceive code={code} />);
		expect(emitAuthCode).toBeCalledWith({code});
	});

	it('should not call emitToken if not all credentials are passed', () => {
		expect(emitToken).not.toBeCalled();
		const token = 'foobarbazbiz';

		render(<OAuthReceive oauth_token={token} />);
		expect(emitToken).not.toBeCalled();
	});

	it('should call emitError if an error was passed', () => {
		expect(emitError).not.toBeCalled();
		const error = 'foobar';
		const errorDescription = 'bazbiz';
		const errorMessage = `${error}: ${errorDescription}`;

		render(
			<OAuthReceive error={error} error_description={errorDescription} />
		);
		expect(emitError).toBeCalledWith({message: errorMessage});
	});
});
