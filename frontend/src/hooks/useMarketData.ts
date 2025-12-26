import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export type StockPrice = {
    symbol: string;
    price: number;
    timestamp: number;
};

export const useMarketData = () => {
    const [prices, setPrices] = useState<Record<string, StockPrice>>({});
    const [isConnected, setIsConnected] = useState(false);

    useEffect(() => {
        // Connect to API Gateway -> Market Data Service
        const socket = new SockJS('http://localhost:8080/ws');

        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log('Connected to WebSocket');
                setIsConnected(true);

                client.subscribe('/topic/prices', (message) => {
                    if (message.body) {
                        const priceUpdate: StockPrice = JSON.parse(message.body);
                        setPrices((prev) => ({
                            ...prev,
                            [priceUpdate.symbol]: priceUpdate,
                        }));
                    }
                });
            },
            onDisconnect: () => {
                console.log('Disconnected');
                setIsConnected(false);
            },
            // debug: (str) => console.log(str),
        });

        client.activate();

        return () => {
            client.deactivate();
        };
    }, []);

    return { prices, isConnected };
};
