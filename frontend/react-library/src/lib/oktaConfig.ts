export const oktaConfig = {
    clientId: '0oaesdmjuiLmNdn805d7',
    issuer: 'https://dev-54106633.okta.com/oauth2/default',
    redirectUri: 'http://localhost:3000/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true,
}