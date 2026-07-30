import 'whatwg-fetch';
import {createMarketoCampaign, updateMarketoCampaign} from '../data-source';

const commonArgs = {
	credentials: {
		oAuthClientId: 'id',
		oAuthClientSecret: 'secret',
		type: 'OAuth 2 Authentication',
	},
	groupId: '23',
	name: 'Marketo',
	status: 'ACTIVE',
	url: 'https://example.mktorest.com/rest',
};

describe('Marketo Data Source API', () => {
	beforeEach(() => {
		window.fetch = jest.fn();
	});

	afterEach(() => {
		fetch.mockClear();
	});

	it('should POST to the marketo-campaign endpoint', () => {
		fetch.mockReturnValue(
			Promise.resolve(new Response('{}', {status: 200}))
		);

		createMarketoCampaign(commonArgs);

		const [requestURL, config] = fetch.mock.calls[0];

		expect(requestURL).toBe(
			'/o/faro/contacts/23/data_source/marketo-campaign'
		);
		expect(config.method).toBe('POST');
		expect(config.body instanceof FormData).toBe(true);
	});

	it('should resolve with the parsed JSON on a 2xx response', async () => {
		fetch.mockReturnValue(
			Promise.resolve(
				new Response(JSON.stringify({id: 'new-id'}), {status: 200})
			)
		);

		await expect(createMarketoCampaign(commonArgs)).resolves.toEqual({
			id: 'new-id',
		});
	});

	it('should resolve with an empty object on a 204 response', async () => {
		fetch.mockReturnValue(
			Promise.resolve(new Response(null, {status: 204}))
		);

		await expect(createMarketoCampaign(commonArgs)).resolves.toEqual({});
	});

	it('should reject with the HTTP status on a non-2xx response', async () => {
		fetch.mockReturnValue(Promise.resolve(new Response('', {status: 403})));

		await expect(createMarketoCampaign(commonArgs)).rejects.toMatchObject({
			status: 403,
		});
	});

	it('should PATCH to the id-scoped marketo-campaign endpoint on update', () => {
		fetch.mockReturnValue(
			Promise.resolve(new Response('{}', {status: 200}))
		);

		updateMarketoCampaign({...commonArgs, id: '7'});

		const [requestURL, config] = fetch.mock.calls[0];

		expect(requestURL).toBe(
			'/o/faro/contacts/23/data_source/7/marketo-campaign'
		);
		expect(config.method).toBe('PATCH');
		expect(config.body instanceof FormData).toBe(true);
	});

	it('should omit the name on an update that does not change it', () => {
		fetch.mockReturnValue(
			Promise.resolve(new Response('{}', {status: 200}))
		);

		updateMarketoCampaign({
			channelsConfiguration: {channels: [], enableAllChannels: true},
			groupId: '23',
			id: '7',
		});

		const [, config] = fetch.mock.calls[0];

		expect(config.body.has('name')).toBe(false);
		expect(config.body.get('name')).not.toBe('undefined');
	});

	it('should send the name on an update that changes it', () => {
		fetch.mockReturnValue(
			Promise.resolve(new Response('{}', {status: 200}))
		);

		updateMarketoCampaign({groupId: '23', id: '7', name: 'Renamed'});

		const [, config] = fetch.mock.calls[0];

		expect(config.body.get('name')).toBe('Renamed');
	});

	it('should reject with the HTTP status on a failed update', async () => {
		fetch.mockReturnValue(Promise.resolve(new Response('', {status: 401})));

		await expect(
			updateMarketoCampaign({...commonArgs, id: '7'})
		).rejects.toMatchObject({
			status: 401,
		});
	});
});
