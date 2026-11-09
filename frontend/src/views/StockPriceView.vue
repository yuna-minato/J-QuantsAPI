<script setup>
import { ref } from 'vue'
import { useRoute, RouterLink } from 'vue-router'

const route = useRoute()

// 銘柄一覧画面から受け取った銘柄コード
const code = route.params.code

// 入力された取得開始日・取得終了日
const startDate = ref('')
const endDate = ref('')

// Spring Bootから取得した四本値
const stockPrices = ref([])

// エラーメッセージ
const errorMessage = ref('')

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
</script>

<template>
    <main>
        <h1>株価四本値</h1>

        <p>{{ code }}</p>
        <p>期間</p>

        <div>
            <input
                id="startDate"
                v-model="startDate"
                type="date"
            />

            <span> ~ </span>

            <input
                id="endDate"
                v-model="endDate"
                type="date"
            />

            <button @click="fetchStockPrices">
                表示
            </button>
        </div>

        <p v-if="errorMessage">
            {{ errorMessage }}
        </p>

        <table v-if="stockPrices.length > 0">
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
                    <td>{{ price.date }}</td>
                    <td>{{ price.open }}</td>
                    <td>{{ price.high }}</td>
                    <td>{{ price.low }}</td>
                    <td>{{ price.close }}</td>
                    <td>{{ price.volume }}</td>
                </tr>
            </tbody>
        </table>
        
        <p>
            <RouterLink to="/">
                銘柄一覧に戻る
            </RouterLink>
        </p>
    </main>
</template>