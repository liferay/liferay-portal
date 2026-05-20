import {Entity} from './types';

export interface EntityDisplay {
	icon: string;
	label: string;
}

const ENTITY_DISPLAY: Record<Entity, EntityDisplay> = {
	[Entity.Accounts]: {
		icon: 'briefcase',
		label: Liferay.Language.get('accounts')
	},
	[Entity.Events]: {
		icon: 'click',
		label: Liferay.Language.get('events')
	},
	[Entity.Individuals]: {
		icon: 'user',
		label: Liferay.Language.get('individuals')
	},
	[Entity.Sites]: {
		icon: 'sites',
		label: Liferay.Language.get('sites')
	},
	[Entity.Users]: {
		icon: 'users',
		label: Liferay.Language.get('users')
	}
};

export function getEntityDisplay(entity: Entity): EntityDisplay {
	return ENTITY_DISPLAY[entity];
}
