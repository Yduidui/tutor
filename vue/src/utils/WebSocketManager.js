export class WebSocketManager {
    constructor(userNo) {
        this.userNo = userNo;
        this.socket = null;
        this.messageHandlers = [];
        this.isConnected = false; // 新增的连接状态标志
        this.connect();
    }

    connect() {
        if (this.socket && this.socket.readyState === WebSocket.OPEN) return;

        // 直接指定后端地址（假设后端运行在 8080 端口）
        const wsUrl = `ws://localhost:8080/ws/chat?userNo=${this.userNo}`;
        this.socket = new WebSocket(wsUrl);


        this.socket.onopen = () => {
            console.log('WebSocket connected');
            this.isConnected = true; // 标记为已连接
        };

        this.socket.onmessage = (event) => {
            const message = JSON.parse(event.data);
            this.messageHandlers.forEach(handler => handler(message));
        };

        this.socket.onerror = (error) => {
            console.error('WebSocket error:', error);
            this.socket.close(); // 会触发 onclose 进行重连
        };

        this.socket.onclose = () => {
            console.log('WebSocket disconnected, retrying...');
            this.isConnected = false; // 标记为已断开
            setTimeout(() => this.connect(), 5000);
        };
    }

    sendMessage(message) {
        return new Promise((resolve, reject) => {
            if (this.isConnected) {
                this.socket.send(JSON.stringify(message));
                resolve(); // 成功发送消息
            } else {
                console.warn("WebSocket not connected.");
                reject("WebSocket not connected.");
            }
        });
    }

    addMessageHandler(handler) {
        this.messageHandlers.push(handler);
    }

    removeMessageHandler(handler) {
        this.messageHandlers = this.messageHandlers.filter(h => h !== handler);
    }

    disconnect() {
        if (this.socket) {
            this.socket.close();
        }
    }
}
