import '@testing-library/jest-dom';
import { TextEncoder, TextDecoder } from 'util';

global.TextEncoder = TextEncoder;
// @ts-expect-error - TextDecoder is not defined in the JSDOM global environment
global.TextDecoder = TextDecoder;
