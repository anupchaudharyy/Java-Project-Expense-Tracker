
from ollama import chat, ChatResponse
import socket
import json
import threading
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class OllamaServer:
    def __init__(self, host="localhost", port=5000):
        self.host = host
        self.port = port
        self.server_socket = None
        self.is_running = False
    
    def start(self):
        try:
            self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            self.server_socket.bind((self.host, self.port))
            self.server_socket.listen(5)
            
            self.is_running = True
            logger.info(f"Ollama server started on {self.host}:{self.port}")
            
            while self.is_running:
                try:
                    client_socket, address = self.server_socket.accept()
                    logger.info(f"Connection from {address}")
                    
                    # Handle client in separate thread
                    client_thread = threading.Thread(
                        target=self.handle_client,
                        args=(client_socket, address)
                    )
                    client_thread.daemon = True
                    client_thread.start()
                    
                except socket.error as e:
                    if self.is_running:
                        logger.error(f"Socket error: {e}")
                    
        except Exception as e:
            logger.error(f"Server error: {e}")
        finally:
            self.stop()
    
    def handle_client(self, client_socket, address):

        buffer = ""
        try:
           
            while True:
                data = client_socket.recv(4096).decode('utf-8')
                if not data:
                    break
                buffer += data
                
                try:
                    request = json.loads(buffer)
                    if request:
                        break 
                except json.JSONDecodeError:
                    
                    continue
            
            if not buffer.strip():
                return

            logger.info(f"Received from {address}: {buffer[:100]}...")
            
            request = json.loads(buffer)
            description = request.get('description', '')
            
            if not description:
                response = {"prediction": "No description provided"}
            else:
                
                prediction = self.get_expense_prediction(description)
                response = {"prediction": prediction}
            
            
            response_json = json.dumps(response)
            client_socket.sendall((response_json + '\n').encode('utf-8'))
            
            logger.info(f"Sent response to {address}")
            
        except json.JSONDecodeError as e:
            logger.error(f"JSON decode error: {e}")
            error_response = {"prediction": "Invalid JSON format"}
            client_socket.sendall(json.dumps(error_response).encode('utf-8'))
            
        except Exception as e:
            logger.error(f"Error handling client {address}: {e}")
            error_response = {"prediction": f"Server error: {str(e)}"}
            client_socket.sendall(json.dumps(error_response).encode('utf-8'))
            
        finally:
            client_socket.close()
    
    def get_expense_prediction(self, description, model='llama3.2:latest'):
      
        try:
            logger.info(f"Attempting to get prediction for description: {description[:100]}...")
            logger.info(f"Using model: {model}")
           
            
            messages = [
                {'role': 'user', 'content': description}

            ]
           
            logger.info("Sending request to Ollama")
            response: ChatResponse = chat(
                model=model, 
                messages=messages
            )
            
            logger.info("Received response from Ollama")

             # Log first 200 chars of response
            logger.info(f"AI Response: {response.message.content[:200]}...") #type: ignore
            return response.message.content
            
        except Exception as e:
            logger.error(f"Ollama service error: {e}")
            return f"AI analysis unavailable: {str(e)}"
    
    def stop(self):
       
        self.is_running = False
        if self.server_socket:
            self.server_socket.close()
        logger.info("Ollama server stopped")

def main():
    
    server = OllamaServer()
    
    try:
        server.start()
    except KeyboardInterrupt:
        logger.info("Received interrupt signal")
        server.stop()

if __name__ == "__main__":
    main()