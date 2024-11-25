// import crypto from 'crypto'

// const generateSecretKey = () => {
//     return crypto.randomBytes(32).toString('hex');
// }

// console.log(generateSecretKey());

import crypto from 'crypto';

// Secret key (should be the same throughout the application)
const secret = '6538a753ff1537b134aa5390c1abfd7f8b0ec7a454d6ad76299e9cf58fbabf92';

// User ID (this should be the same for both generation and validation)
const userId = '668d6dd89e00f2b57545e9bd';

// Generate the HMAC hash for the userId
const userIdEncrypted = crypto.createHmac('sha256', secret)
                              .update(userId)
                              .digest('hex')
                              .substring(0, 6)
                              .toUpperCase();

console.log('userIdEncrypted:', userIdEncrypted); // Example output: 'A1B2C3'

// Validate the user ID by comparing the hash
const userIdValid = crypto.createHmac('sha256', secret)
                          .update(userId)
                          .digest('hex')
                          .substring(0, 6)
                          .toUpperCase() === userIdEncrypted;

console.log('userIdValid:', userIdValid); // Should output: true

// Repeat the process to ensure consistency
const userIdEncryptedAgain = crypto.createHmac('sha256', secret)
                                   .update(userId)
                                   .digest('hex')
                                   .substring(0, 6)
                                   .toUpperCase();

console.log('userIdEncryptedAgain:', userIdEncryptedAgain);