import {useBodyWidthBelow} from 'shared/hooks/useBodyWidthBelow';

/**
 * Same breakpoint Clay's `SidePanel` uses to switch to its mobile overlay,
 * which traps focus while the panel is open.
 */
export const useIsMobile = () => useBodyWidthBelow(768);
