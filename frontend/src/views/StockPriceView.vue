<script setup>
import { ref, computed } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { Line } from 'vue-chartjs'

import {
    Chart as ChartJS,
    Title,
    Tooltip,
    Legend,
    LineElement,
    PointElement,
    CategoryScale,
    LinearScale
} from 'chart.js'

ChartJS.register (
    Title,
    Tooltip,
    Legend,
    LineElement,
    PointElement,
    CategoryScale,
    LinearScale
)

const route = useRoute()

// 銘柄一覧画面から受け取った銘柄コード、銘柄名
const code = route.params.code
const coName = route.query.coName

// 入力された取得開始日・取得終了日
const startDate = ref('')
const endDate = ref('')

// Spring Bootから取得した四本値
const stockPrices = ref([])

// エラーメッセージ
const errorMessage = ref('')

// データをグラフ用に変換
const chartData = computed(() => ({
    labels: stockPrices.value.map(price => formatDate(price.date)),

    datasets: [
        {
            label: '始値',
            data: stockPrices.value.map(price => price.open),
            borderColor: 'orange',
            backgroundColor: 'orange',
            tension: 0.2
        },
        {
            label: '終値',
            data: stockPrices.value.map(price => price.close),
            borderColor: 'green',
            backgroundColor: 'green',
            tension: 0.2
        },
        {
            label: '高値',
            data: stockPrices.value.map(price => price.high),
            borderColor: 'red',
            backgroundColor: 'red',
            tension: 0.2
        },
        {
            label: '安値',
            data: stockPrices.value.map(price => price.low),
            borderColor: 'blue',
            backgroundColor: 'blue',
            tension: 0.2
        }
    ]
}))

// グラフの設定
const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,

    plugins: {
        title: {
            display: true,
            text: '【株価四本値推移】',
            font: {
                size: 24
            }
        },

        legend: {
            display: true,
            labels: {
                font: {
                    size: 16
                }
            }
        }
    }
}

// 株価四本値を取得
const fetchStockPrices = async () => {
    try {
        errorMessage.value = ''

        if (!startDate.value || !endDate.value) {
            errorMessage.value = '取得期間内の日付を指定してください'
            return
        }

        const url = 
            'http://localhost:8080/api/stockPrices' +
            '?code=' + code +
            '&startDate=' + startDate.value +
            '&endDate=' + endDate.value

        const response = await fetch(url)

        if (!response.ok) {
            throw new Error('株価四本値の取得に失敗しました')
        }

        stockPrices.value = await response.json()

        if (stockPrices.value.length === 0) {
            errorMessage.value = '表示できるデータが存在しません'
        }
    } catch (error) {
        console.error(error)
        errorMessage.value = '株価四本値の取得中にエラーが発生しました'
    }
}

// 日付の表示方法変換
const formatDate = (date) => {
    return date.replaceAll('-', '/')
}

</script>

<template>
    <main class="stock-price-page">

        <p class="stock-info">
            {{ code }}　{{ coName }}
        </p>
        
        <div class="period">
            <div class="period-title">期間</div>

            <div class="period-input">
            <input
                class="date-input"
                v-model="startDate"
                type="date"
            />

            <span class="date-separator"> ~ </span>

            <input
                class="date-input"
                v-model="endDate"
                type="date"
            />

            <button
            class="display-button"
            @click="fetchStockPrices"
            >
                表示
            </button>
            </div>
        </div>

        <p v-if="errorMessage">
            {{ errorMessage }}
        </p>

        <div
            v-if="stockPrices.length > 0"
            class="chart-area"
        >
            <Line
                :data="chartData"
                :options="chartOptions"
            />
        </div>

        <table 
            v-if="stockPrices.length > 0"
            class="price-table"    
        >
            <thead>
                <tr>
                    <th>日付</th>
                    <th>始値</th>
                    <th>高値</th>
                    <th>安値</th>
                    <th>終値</th>
                    <th>出来高</th>
                </tr>
            </thead>

            <tbody>
                <tr
                    v-for="price in stockPrices"
                    :key="price.date"
                >
                    <td>{{ formatDate(price.date) }}</td>
                    <td>{{ price.open }}</td>
                    <td>{{ price.high }}</td>
                    <td>{{ price.low }}</td>
                    <td>{{ price.close }}</td>
                    <td>{{ price.volume }}</td>
                </tr>
            </tbody>
        </table>
        
        <div class="back-button-area">
            <RouterLink to="/" class="back-button">
                銘柄一覧に戻る
            </RouterLink>
        </div>
    </main>
</template>

<style scoped>
    .price-table {
        border-collapse: collapse;
        width: 100%;
        margin-bottom: 50px;
    }

    .price-table th,
    .price-table td {
        border: 1px solid #cccccc;
        padding: 8px-12px;
    }

    .price-table th {
        background-color: #e3f9fdf3;
        front-weight: bold;
        text-align: center;
    }

    .price-table td {
        text-align: right;
    }

    .price-table td:first-child {
        text-align: center;
    }

    .back-button-area {
        text-align: center;
        margin-bottom: 30px;
    }

    .back-button {
        display: inline-block;
        font-weight: bold;
        padding: 10px 24px;
        background-color: #60bbdc;
        color: white;
        text-decoration: none;
        border-radius: 6px;
        front-weight: bold;
    }

    .back-button:hover {
        background-color: #2f9ca5;
    }

    .period {
        margin-top: 30px;
        margin-bottom: 30px;
    }

    .period-title {
        font-size: 20px;
        font-weight: bold;
        margin-bottom: 10px;
    }

    .period-input {
        display: flex;
        align-items: center;
    }

    .display-button {
        margin-left: 16px;
        padding: 9px 24px;

        font-size: 17px;
        font-weight: bold;

        background-color: #60bbdc;
        color: white;

        border: none;
        border-radius: 6px;

        cursor: pointer;
    }

    .display-button:hover {
        background-color: #2f9ca5;
    }

    .stock-price-page {
        width: 90%;
        max-width: 1400px;
        margin: 0 auto;
        padding: 70px 0 40px;
    }

    .chart-area {
        width: 100%;
        height: 450px;
        margin: 30px 0 50px;
    }

    .stock-info {
        font-size: 22px;
        font-weight: bold;
    }

    .date-input {
        font-size: 17px;
        padding: 8px 12px;
    }

    .date-separator {
        font-size: 18px;
        margin: 0 8px;
    }
</style>