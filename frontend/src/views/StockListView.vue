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

const pageInput = ref(1)

const goToPage = () => {
    const page = Number(pageInput.value)

    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page
        pageInput.value = page
    }
}

// 前へ
const previousPage = () => {
    if (currentPage.value > 1) {
        currentPage.value--
        pageInput.value = currentPage.value
    }
}

// 次へ
const nextPage = () => {
    if (currentPage.value < totalPages.value) {
        currentPage.value++
        pageInput.value = currentPage.value
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
    pageInput.value = 1
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
    pageInput.value = 1
}

onMounted(() => {
  fetchStocks()
})
</script>

<template>
    <main class="stock-list-page">
        <h1>銘柄一覧</h1>
            <div class="search-area">
                <label for="stockSearch" class="search-label">
                    銘柄名
                </label>

                <input
                    v-model="searchText"
                    type="text"
                    class="search-input"
                    @keyup.enter="searchStocks"
                />

                <button 
                    class="search-button"
                    @click="searchStocks"
                >
                    検索
                </button>

                <button
                    class="clear-button"
                    @click="clearSearch"
                >
                    クリア
                </button>
            </div>

        <p class="stock-count">
            取得件数：{{ filteredStocks.length }}件
        </p>

        <table class="stock-table">
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
                    class="stock-code-link"
                        :to="{
                            name: 'stockPrices',
                            params: { code: stock.code },
                            query: { coName: stock.coName }
                        }"
                    >
                        {{ stock.code }}
                    </RouterLink>
                </td>
                <td>{{ stock.coName }}</td>
            </tr>
        </tbody>
        </table>

        <div class="pagination">
            <button
            class="page-button"
            @click="previousPage"
            :disabled="currentPage === 1"
            >
            前へ
            </button>

            <div class="page-jump">
                <input
                    v-model="pageInput"
                    type="number"
                    min="1"
                    :max="totalPages"
                    class="page-input"
                    @keyup.enter="goToPage"
                />

                <span>/ {{ totalPages }}</span>

                <button
                    class="jump-button"
                    @click="goToPage"
                >
                    移動
                </button>
            </div>

            <button
                class="page-button"
                @click="nextPage"
                :disabled="currentPage === totalPages"
            >
            次へ
            </button>
        </div>
    </main>
</template>

<style scoped>
    .stock-list-page {
        width: 90%;
        max-width: 1400px;
        margin: 0 auto;
        padding: 50px 0 30px;
    }

    .search-area {
        display: flex;
        align-items: center;
        gap: 16px;
        margin:25px 0 30px;
    }

    .search-input {
        width: 300px;
        padding: 9px 12px;
        font-size: 16px;
    }

    .search-button,
    .clear-button {
        padding: 9px 24px;
        font-size: 16px;
        border-radius: 6px;
        cursor: pointer;
    }

    .search-button {
        font-weight: bold;
        background-color: #60bbdc;
        color: white;
        border: none;
    }

    .clear-button {
        font-weight: bold;
        background-color: white;
        color: #333;
        border: 1px solid #999;
    }

    .stock-table {
        width: 100%;
        border-collapse: collapse;
        text-align: center;
    }

    .stock-table th {
        padding: 12px;
        font-size: 17px;
        text-align: center;

        border-bottom: 2px solid #666;
    }

    .stock-table th:first-child,
    .stock-table td:first-child {
        width: 30%;
        text-align: center;
    }

    .stock-table th:nth-child(2),
    .stock-table td:nth-child(2) {
        width: 70%;
    }

    .stock-table td {
        padding: 10px 12px;
    }

    .pagination {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 20px;

        margin-top: 30px;
        margin-bottom: 30px;
    }

    .page-button {
        padding: 10px 28px;
        font-size: 16px;
        font-weight: bold;

        background-color: #60bbdc;
        color:white;

        border: none;
        border-radius: 6px;

        cursor: pointer;
    }

    .page-button:hover:not(:disabled) {
        background-color: #2f9ca5;
    }

    .page-button:disabled {
        background-color: #cccccc;
        cursor: default;
    }

    .stock-code-link {
        display: inline-block;
        min-width: 80px;
        padding: 4px 8px;

        font-size: 17px;
        font-weight: 550;

        color: #60bbdc;
        text-align: center;
        text-decoration: none;

        border-radius: 4px;
    }

    .stock-code-link:hover {
        color: #2f9ca5;
        text-decoration: underline;
    }

    .search-label {
        font-size: 17px;
        font-weight: bold;
    }

    .stock-count {
        font-size: 17px;
        margin: 20px 0;
    }

    .page-jump {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 17px;
    }

    .page-input {
        width: 65px;
        padding: 8px;
        font-size: 17px;
        text-align: center;
    }

    .jump-button {
        padding: 9px 16px;
        font-size: 16px;
        background-color: #ffffff;
        border: 1px solid #2f9ca5;
        color: #2f9ca5;
        border-radius: 6px;
        cursor: pointer;
    }
 </style>