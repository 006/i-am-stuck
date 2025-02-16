import { EXAUHST, BAD, GREAT, GOOD, NEUTRAL, SOS, IN_TALK, SAVED } from "./icon"

export function getMood(bites: number) {
    switch (bites) {
        case -2: return EXAUHST;
        case -1: return BAD;
        case 1: return GOOD;
        case 2: return GREAT;
        case 0: return NEUTRAL;
    }
}

export function getStatusIcon(state: number) {
    switch (state) {
        case 0: return SOS;
        case 1: return IN_TALK;
        case 2: return SAVED;
    }
}

export function getStatusText(state: number) {
    switch (state) {
        case 0: return "Stuck, please help...";
        case 1: return "In talk, help is on the way...";
        case 2: return "Blessed, Saved.";
    }
}