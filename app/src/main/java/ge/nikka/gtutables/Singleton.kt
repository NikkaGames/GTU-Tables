package ge.nikka.gtutables

class Singleton {
    private var localData: String? = ""
    private var tableId: String? = ""
    var data: String?
        get() {
            if (localData == null) return "NOT_FOUND"
            if (localData!!.isEmpty()) return "NOT_FOUND"
            else return localData
        }
        set(data) {
            localData = data
        }

    var table: String?
        get() {
            if (tableId == null) return "NOT_FOUND"
            if (tableId!!.isEmpty()) return "NOT_FOUND"
            else return tableId
        }
        set(data) {
            tableId = data
        }

    fun cleanUp() {
        localData = ""
        tableId = ""
    }

    companion object {
        var instance: Singleton = Singleton()
    }
}
