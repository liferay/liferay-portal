import {getActionFromEventId, getEventId} from '../activity-keys';

describe('activity-keys', () => {
	describe('getEventId', () => {
		it('should resolve the analytics eventId for supported pairs', () => {
			expect(getEventId('WebContent', 'click')).toBe('webContentClicked');
			expect(getEventId('Blog', 'comment')).toBe('commentPosted');
			expect(getEventId('ObjectEntry', 'download')).toBe(
				'objectEntryDownloaded'
			);
			expect(getEventId('Page', 'view')).toBe('pageViewed');
		});

		it('should name the pairs nothing emits yet by the same convention', () => {
			expect(getEventId('Document', 'click')).toBe('documentClicked');
			expect(getEventId('Form', 'download')).toBe('formDownloaded');
			expect(getEventId('ObjectEntry', 'click')).toBe(
				'objectEntryClicked'
			);
			expect(getEventId('WebContent', 'submit')).toBe(
				'webContentSubmitted'
			);
		});

		it('should give every asset type an eventId for every action', () => {
			['Blog', 'Document', 'Form', 'ObjectEntry', 'WebContent'].forEach(
				(applicationId) =>
					[
						'click',
						'comment',
						'download',
						'impression',
						'submit',
						'view',
					].forEach((action) =>
						expect(getEventId(applicationId, action)).toBeTruthy()
					)
			);
		});

		it('should still return an empty string without an action', () => {
			expect(getEventId('Document', undefined)).toBe('');
		});
	});

	describe('getActionFromEventId', () => {
		it('should resolve stored eventIds back to their generic action', () => {
			expect(getActionFromEventId('webContentClicked')).toBe('click');
			expect(getActionFromEventId('commentPosted')).toBe('comment');
			expect(getActionFromEventId('pageViewed')).toBe('view');
		});

		// The round trip the empty eventId used to break: the criterion has to
		// come back as Click, not as an attribute that no longer exists.

		it('should resolve the eventIds of the pairs nothing emits yet', () => {
			expect(getActionFromEventId('documentClicked')).toBe('click');
			expect(getActionFromEventId('formImpressionMade')).toBe(
				'impression'
			);
			expect(getActionFromEventId('objectEntrySubmitted')).toBe('submit');
		});
	});
});
