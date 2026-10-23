import type {Size} from '@clayui/modal/lib/types';

/**
 * The widest fixed size of a Clay modal (1140px). Clay's CSS defines
 * `modal-xl`, but the `size` type of `ClayModal` leaves it out.
 */
export const MODAL_SIZE_XL = 'xl' as unknown as Size;
