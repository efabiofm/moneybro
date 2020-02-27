const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

exports.sendTransactionNotification = functions.firestore.document('/transactions/{transactionId}')
    .onCreate((snap, context) => {
        const data = snap.data();
        return admin.firestore().doc('users/' + data.receiverId).get().then(userDoc => {
            const fcmToken = userDoc.get('fcmToken');

            const payload = {
                notification: {
                    title: data.creatorName + ' solicita tu confirmación',
                    body: 'Se ha realizado un ' + data.type + ' por ₡' + data.amount,
                    clickAction: 'HomeActivity'
                }
            };

            return admin.messaging().sendToDevice(fcmToken, payload).then(response => {
                return response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                        console.error('Error sending message', error);
                    } else {
                        console.log('Message sent to ' + fcmToken);
                    }
                });
            });
        });
    });

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
