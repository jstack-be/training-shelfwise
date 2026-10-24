// @ts-check
// Mechanical front-end conventions from CLAUDE.md. Existing violations are baselined in eslint-suppressions.json;
// only new ones fail. Never add entries to that file by hand.
const tseslint = require('typescript-eslint');
const angular = require('angular-eslint');

const HEX_COLOUR = '/#[0-9a-fA-F]{3,8}\\b/';
const PIXEL_SPACING = '/(^|[\\s;{])(margin|padding|gap|row-gap|column-gap|inset|top|right|bottom|left)(-[a-z]+)*\\s*:[^;]*\\b[1-9][0-9]*px/';

module.exports = tseslint.config({
  ignores: ['src/**/*.spec.ts'],
}, {
  files: ['src/**/*.ts'],
  languageOptions: { parser: tseslint.parser },
  plugins: { '@angular-eslint': angular.tsPlugin },
  rules: {
    '@angular-eslint/prefer-standalone': 'error',
    '@angular-eslint/prefer-on-push-component-change-detection': 'error',
    'no-restricted-syntax': [
      'error',
      {
        selector: 'Decorator[expression.callee.name=/^(Input|Output)$/]',
        message: 'Use input() and output() instead of the @Input()/@Output() decorators.',
      },
      {
        selector: `Property[key.name='styles'] TemplateElement[value.raw=${HEX_COLOUR}]`,
        message: 'No hex colours in a component; use a --sw-color-* token (frontend-folio skill).',
      },
      {
        selector: `Property[key.name='styles'] TemplateElement[value.raw=${PIXEL_SPACING}]`,
        message: 'No pixel spacing in a component; use a --sw-space-* token (frontend-folio skill).',
      },
    ],
  },
}, {
  files: ['src/**/*.ts'],
  ignores: ['src/app/core/**', 'src/app/app.config.ts'],
  rules: {
    'no-restricted-imports': ['error', {
      paths: [{
        name: '@angular/common/http',
        importNames: ['HttpClient'],
        message: 'API calls are plain functions in src/app/core/api/*.api.ts using apiGet/apiPost/apiPut.',
      }],
    }],
  },
});
