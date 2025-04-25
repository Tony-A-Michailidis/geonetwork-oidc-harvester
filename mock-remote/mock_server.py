from http.server import HTTPServer, BaseHTTPRequestHandler

ACCESS_TOKEN = "test-token-123"  # Replace with real token if testing live

class TokenHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        auth = self.headers.get('Authorization')
        if auth == f"Bearer {ACCESS_TOKEN}":
            self.send_response(200)
            self.send_header("Content-Type", "application/xml")
            self.end_headers()
            with open("remote-metadata.xml", "rb") as f:
                self.wfile.write(f.read())
        else:
            self.send_response(401)
            self.end_headers()
            self.wfile.write(b"Unauthorized")

if __name__ == "__main__":
    print("Mock remote metadata server running at http://localhost:9000/remote-metadata.xml")
    server = HTTPServer(('0.0.0.0', 9000), TokenHandler)
    server.serve_forever()
