import React from 'react';
import { ArrowUp, ArrowDown } from 'lucide-react';
import { cn } from '@/lib/utils'; // We need to create util first? Or inline it. I'll inline for simplicity.

type PriceCardProps = {
    symbol: string;
    price: number;
    prevPrice?: number;
};

export const PriceCard = ({ symbol, price, prevPrice }: PriceCardProps) => {
    const isUp = prevPrice ? price >= prevPrice : true;
    const color = isUp ? 'text-green-500' : 'text-red-500';

    return (
        <div className="bg-slate-800 p-4 rounded-xl border border-slate-700 shadow-lg min-w-[200px]">
            <div className="text-slate-400 text-sm font-medium">{symbol}</div>
            <div className="flex items-center gap-2 mt-1">
                <span className="text-2xl font-bold text-white">${price.toFixed(2)}</span>
                {isUp ? <ArrowUp className="w-4 h-4 text-green-500" /> : <ArrowDown className="w-4 h-4 text-red-500" />}
            </div>
        </div>
    );
};
