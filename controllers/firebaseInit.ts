import * as admin from 'firebase-admin';
// import serviceAccount from '../confidential/nodemcunotify-a9a23-firebase-adminsdk-2tl2i-8f4e0af4e4.json';

admin.initializeApp({
    credential: admin.credential.cert({
        "type": "service_account",
        "project_id": "nodemcunotify-a9a23",
        "private_key_id": "8f4e0af4e461fbac9688cc5237d863d3140894ad",
        "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCRQhZ+Bh4i2uZ+\nd2ccE9aTk/fE+3ZITcBl82eRI+l4Us2DxhrApFX77fkya5jgum8m2+TYQarffdpl\nTW5PyFJXSUc4iwP1nVxyjMSOETK5HJJj3aqFKEjaCsdoZLtnwQNJmG1Fv6AFAshd\nBh8UiqncrDY3sTFJvFYSe1ebMyKVwGF+E4fe+5gnGdXALoavPtRN4yuzqLx7EWnB\ndx9bJByuY+zveBVDOUejzf3VCJ9ocKGYCJI33c0Q8dwtk9IAjFwGoraE5Lnb4ixx\ncsVdhNzsW5EyYeMwrTpxam+cpub2RsjeTwGjPvHerUF6A7zFeSaP9gf7MVXIw43B\n0ho1vrBVAgMBAAECggEANwOVqzM79K19QIaOaablzMzsuREX8jhoVvSB1AmK4gRS\nS8ca2L0kx56Qk9UGeI6DjG8Z1AHC6pQqw9U4o0TWUrKxI6wa2so3ejbwNnIzCMLW\nerY4X+gq91G/xjzocVo7mYL+V71EqMEuC3VHDiuaO7aJbjxQ04FeA+/s9R2/yoEY\n+F+edAZwrrKe4Kdzdm36XJUOqT7XmHSrZpkjVjdVTZyvcel7YWhfHyLAnHe1NlWk\nDZOV8F/FG0SWVV7zle7/cUeq8CuEdgcYUHe4dL8tfS2Wng3PLeYhO7rT9cStFGYk\nEgXfc+HVifGMVzbns4KCKAFdwusc3g6gFzp6eQ9I3QKBgQDJJo6O806B4qXU2oMR\nXDAWUJClXaNzeiNByq1PJRab6xyLssFDNNWOPOL4QNEFcxl9qyoKrObLP5+Z3GY8\n5C1OYj+WcGTPcth6IGNCXRyGgsZkrzfR3HLE53mt5dy5l9JF/YGd4X0BvsumWw7F\nKXyslZ1HmMKWumN6h0ra11S7GwKBgQC43exkHbhSYATYYat1BCmUt9LGYcNPIyYe\n2qgrk+7LFb+bljVJMU3yPITljV2R6cJrxPARaI+Vz4ely2oyiEIpCwzTvRhuKgEU\nub7rjFmA+Vi8oaXM7jtFxt/AmrLWq4SV77oeVj45zxeOA7SG9QtNCQHxN7bFJl7b\nRwsfm18JTwKBgFRdboc4NuI1DDZ1G2nJj4Wcbr5cwrQpMuZiOkQqmwv0FTnmVDZ6\nz2Lj2JIkpUYy2yshAfYORJJUcf0XOm4piXMJs//FzhJP6cl3EgqAgPlNmiYacz/6\nxMwfBaLQ+L2ClreyNo3gSAzWWBXCtTQuTVMZQSkJQfvz2stGOTgF/kynAoGAHrFx\n6xloHf0lMMvejLoZMUDn9NoMdFownRTOsRWJoxxESM/CnxlZiKt1oSs8atp11jDH\nbwx+MnBXZDaGtkcbPPdBtPIDWV8N+OPyB+ewgJJlokPwZDr1ils/kRneMXgetkos\nC5dkE3MHxSNmWLXcn3isYuFOLiN1wlBkT2YbfE8CgYEAqBAAfyyC9+JB7eeoE3zp\ne0oh2Oqo6POYvhGWthn+dv0p95QKB8afdnNBhUl+KKnrNY9jEMvyLMq1TYRUx4Xu\nQibD8Ziqw6i7bObYTHq0sloKyn2KZ+U7+lzxaHfgoH47XwX5glCrGhujmy8XIrkY\nvnlv4+j5cegG6XLoDYzAqQw=\n-----END PRIVATE KEY-----\n",
        "client_email": "firebase-adminsdk-2tl2i@nodemcunotify-a9a23.iam.gserviceaccount.com",
        "client_id": "112016190491472305943",
        "auth_uri": "https://accounts.google.com/o/oauth2/auth",
        "token_uri": "https://oauth2.googleapis.com/token",
        "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
        "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-2tl2i%40nodemcunotify-a9a23.iam.gserviceaccount.com",
        "universe_domain": "googleapis.com"
      } as admin.ServiceAccount),
    databaseURL: "https://nodemcunotify-a9a23.firebaseio.com"
});


const db = admin.firestore();
const messaging = admin.messaging();

export {admin, db, messaging};