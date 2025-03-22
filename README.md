# HiveMQ TLS Client for Android

A secure MQTT client application for Android that demonstrates TLS/SSL connectivity with client certificate authentication using the HiveMQ MQTT client library version 1.3.5. This example application connects to any compatible MQTT broker with TLS support and displays messages from subscribed topics.

## Features

- Secure MQTT connection using TLS with client certificate authentication
- Built with HiveMQ MQTT Client library version 1.3.5
- Real-time display of crash alerts in a scrollable list
- Connection status monitoring with connect/disconnect functionality
- Support for MQTT Quality of Service (QoS) level 1 (at least once)
- JSON payload parsing and visualization
- Modern UI built with Jetpack Compose

## Project Structure

### Key Components

- `MainActivity.kt`: Main UI and application entry point
- `MqttClientManager.kt`: Handles MQTT connection, subscriptions, and message processing
- `CrashAlert.kt`: Data models for parsing the MQTT message payloads

### Directory Structure

```
app/
├── src/
│   ├── main/
│   │   ├── assets/
│   │   │   └── certs/       # Certificates and keys for TLS (you need to generate these)
│   │   │       ├── client.crt
│   │   │       ├── client.key
│   │   │       └── rootCA.crt
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── voidstorm/
│   │   │           └── hivemqtls/
│   │   │               ├── MainActivity.kt
│   │   │               ├── MqttClientManager.kt
│   │   │               └── CrashAlert.kt
│   │   ├── AndroidManifest.xml
│   │   └── res/            # Android resources
└── build.gradle.kts       # Project dependencies and configuration
```

## ⚠️ Important Notice for Using This Project

Before using this project, you must make several key changes to adapt it to your environment:

1. **Generate Your Own Certificates**:
   - The repository does not include certificates for security reasons
   - You need to generate your own certificates as described in the setup section
   
2. **Update MQTT Connection Parameters**:
   - Change the broker IP address in `MqttClientManager.kt`
   - The default is set to a local IP (192.168.1.2) which needs to be changed to your broker's address
   - Update topics as needed for your application

3. **Adapt the Payload Structure**:
   - The app expects specific JSON payloads (see format below)
   - Modify the `CrashAlert.kt` data class if your payload structure differs

## Technical Details

### MQTT Configuration

- MQTT Client: HiveMQ MQTT Client library v1.3.5
- MQTT Broker: Any broker supporting MQTT with TLS on port 8883
- Topic: `iot/crashalerts` (can be customized)
- QoS: Level 1 (at least once delivery)
- TLS Version: 1.2+
- Client Certificate Authentication

### JSON Message Format

The application expects messages in the following JSON format:

```json
{
  "msgType": "CRASH_REPORT",
  "crashID": 3,
  "acceleration": 0.947031,
  "tiltAngle": 177.3504,
  "timestamp": "4000-02-09 04:26:30",
  "crashType": "Severe Tilt",
  "crashStatus": "CRASH_CONFIRMED",
  "location": {
    "lat": 1.3521,
    "lon": 103.8198
  }
}
```

## Setup Instructions

### Prerequisites

- Android Studio (latest version recommended)
- An MQTT broker with TLS support
- OpenSSL for certificate generation

### Generating Certificates

You must generate your own certificates for this application. Here's how to generate test certificates:

```bash
# Create directories
mkdir -p certs/CA
cd certs

# Generate CA key and certificate
openssl genrsa -out CA/ca.key 2048
openssl req -new -x509 -days 3650 -key CA/ca.key -out CA/ca.crt -subj "/CN=MQTT Test CA"

# Generate client key and CSR
openssl genrsa -out client.key 2048
openssl req -new -key client.key -out client.csr -subj "/CN=mqtt-client"

# Sign client certificate with CA
openssl x509 -req -days 365 -in client.csr -CA CA/ca.crt -CAkey CA/ca.key \
  -CAcreateserial -out client.crt

# Copy the certificates to the app's assets folder
mkdir -p /path/to/project/app/src/main/assets/certs
cp client.crt client.key CA/ca.crt /path/to/project/app/src/main/assets/certs/
# Rename CA/ca.crt to rootCA.crt in the destination
mv /path/to/project/app/src/main/assets/certs/ca.crt /path/to/project/app/src/main/assets/certs/rootCA.crt
```

### Setting Up an MQTT Broker

You can use any MQTT broker that supports TLS with client authentication. Here's a quick example using Mosquitto, but you can use other brokers like HiveMQ, EMQ X, or cloud services:

```
# Example Mosquitto configuration (/etc/mosquitto/mosquitto.conf)
listener 8883
cafile /path/to/ca.crt
certfile /path/to/server.crt
keyfile /path/to/server.key
require_certificate true
use_identity_as_username true
```

For cloud-based MQTT services, follow their documentation for setting up TLS connections with client certificates.

### Running the Application

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/hivemq-tls-android.git
   ```

2. Generate certificates as described above and place them in the `app/src/main/assets/certs/` directory:
   - `client.crt`: Client certificate
   - `client.key`: Client private key
   - `rootCA.crt`: Certificate Authority certificate

3. Configure your MQTT broker connection in `MqttClientManager.kt`:
   ```kotlin
   // CHANGE THESE VALUES to match your environment
   private const val MQTT_BROKER_HOST = "your.broker.ip.address" // Change from 192.168.1.2
   private const val MQTT_BROKER_PORT = 8883
   private const val MQTT_TOPIC = "your/topic" // Change if needed
   ```

4. Build and run the application on an Android device or emulator

## Testing the Connection

To test if your app is connecting properly, you can publish test messages using any MQTT client or the command line:

```bash
# Using mosquitto_pub (if you're using Mosquitto)
mosquitto_pub --cafile ca.crt --cert client.crt --key client.key \
  -h your.broker.ip.address -p 8883 \
  -t iot/crashalerts -m '{"msgType":"CRASH_REPORT","crashID":1,"acceleration":1.5,"tiltAngle":45.7,"timestamp":"2023-03-23 14:22:30","crashType":"Impact","crashStatus":"CRASH_DETECTED","location":{"lat":37.7749,"lon":-122.4194}}'
```

You can also use other MQTT clients like MQTT Explorer, HiveMQ Client, or any other tool that supports MQTT with TLS.

## Customizing the Application

### Changing the Message Format

If your MQTT messages use a different format, modify the `CrashAlert.kt` data class:

1. Update the data class properties to match your payload structure
2. Update the SerializedName annotations if needed
3. Modify the UI to display your custom data

### Using Different Certificate Formats

If you have certificates in different formats, you might need to adapt the loading methods in `MqttClientManager.kt`. For example:

- For PKCS#12 (.p12) files, use `KeyStore.getInstance("PKCS12")`
- For different private key formats, adjust the `loadPrivateKey()` method

## Dependencies

- HiveMQ MQTT Client: `com.hivemq:hivemq-mqtt-client:1.3.5`
- Gson: `com.google.code.gson:gson:2.10.1`
- Jetpack Compose UI toolkit

## Troubleshooting

### Common Issues

- **Connection Failures**: Check broker address and port, verify certificates are valid
- **Certificate Problems**: 
  - Ensure your CA certificate matches what's used by the broker
  - Check that the private key format is correctly loaded
  - Verify certificate dates are valid
- **Parse Errors**: Verify your JSON structure matches the CrashAlert data class

### Debugging Tips

The application uses Android's logging system with the tag `MqttClientManager`. Monitor logs with:

```
adb logcat -s MqttClientManager:D
```

For certificate issues, look for SSL handshake failures in the logs.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

- HiveMQ for the MQTT client library
- Jetpack Compose for the modern UI toolkit
- MQTT.org for the MQTT protocol

---

*Note: This application is provided as an educational example. When adapting for production, implement proper security practices including secure certificate management and credential protection.*