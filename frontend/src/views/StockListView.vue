<script setup>
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'

const stocks = ref([])

// 銘柄一覧を取得
const fetchStocks = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/stocks')
    const data = await response.json()

    stocks.value = data
  } catch (error) {
    console.error('銘柄取得エラー:', error)
  }
}

// 現在のページ
const currentPage = ref(1)

// 1ページの表示件数
const pageSize = 50

// 現在のページに表示する50件を取り出す
const displayedStocks = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    const end = start + pageSize

    return filteredStocks.value.slice(start, end)
})

// 全ページ数
const totalPages = computed(() => {
    return Math.ceil(filteredStocks.value.length / pageSize)
})

// 前へ
const previousPage = () => {
    if (currentPage.value > 1) {
        currentPage.value--
    }
}

// 次へ
const nextPage = () => {
    if (currentPage.value < totalPages.value) {
        currentPage.value++
    }
}

// 検索入力値
const searchText = ref('')

// 実際に検索する文字
const searchKeyword = ref('')

// 検索
const searchStocks = () => {
    searchKeyword.value = searchText.value
    currentPage.value = 1
}

// 絞り込み処理
const filteredStocks = computed(() => {
    if (searchKeyword.value === '') {
        return stocks.value
    }

    return stocks.value.filter(stock => stock.coName.includes(searchKeyword.value))
})

// クリア処理
const clearSearch = () => {
    searchText.value = ''
    searchKeyword.value = ''
    currentPage.value = 1
}

onMounted(() => {
  fetchStocks()
})
</script>

<template>
    <main>
        <h1>銘柄一覧</h1>
            <div>
                <label for="stockSearch">
                    銘柄名
                </label>

                <input
                    id="stockSearch"
                    v-model="searchText"
                    type="Text"
                />

                <button @click="searchStocks">
                    検索
                </button>

                <button @click="clearSearch">
                    クリア
                </button>
            </div>

        <p>取得件数：{{ stocks.length }}件</p>

        <table>
        <thead>
            <tr>
                <th>銘柄コード</th>
                <th>銘柄名</th>
            </tr>
        </thead>

        <tbody>
            <tr v-for="stock in displayedStocks" :key="stock.code">
                <td>
                    <RouterLink
                        :to="{
                            name: 'stockPrices',
                            params: { code: stock.code }
                        }"
                    >
                        {{ stock.code }}
                    </RouterLink>
                </td>
                <td>{{ stock.coName }}</td>
            </tr>
        </tbody>
        </table>

        <div>
            <button
            @click="previousPage"
            :disabled="currentPage === 1"
            >
            前へ
            </button>

            <span>
            {{ currentPage }} / {{ totalPages }}
            </span>

            <button
                @click="nextPage"
                :disabled="currentPage === totalPages"
            >
            次へ
            </button>
        </div>
    </main>
</template>