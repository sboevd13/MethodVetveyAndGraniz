import java.util.PriorityQueue
const val m = 1000000
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {


    val matrix = arrayOf(
        intArrayOf(m, 20, 18, 12, 8),
        intArrayOf(5, m, 14, 7, 11),
        intArrayOf(12, 18, m, 6, 11),
        intArrayOf(11, 17, 11, m, 12),
        intArrayOf(5, 5, 5, 5, m)
    )

    /*
    val matrix = arrayOf(
        intArrayOf(m, 90, 80, 40, 100),
        intArrayOf(60, m, 40, 50, 70),
        intArrayOf(50, 30, m, 60, 20),
        intArrayOf(10, 70, 20, m, 50),
        intArrayOf(20, 40, 50, 20, m)
    )

     */

    val result = mvg(matrix)
    println("\nМинимальная стоимость: ${result.h}")
    println("Маршрут:")
    result.pointArr.forEach { println("${it.first + 1} -> ${it.second + 1}") }

}

fun mvg(matrix: Array<IntArray>) : Node {
    /*
    val priorityQueue = PriorityQueue<Node>()
    var root = Node(matrix,0)
    var reductionMatrixH = reductionMatrixAndFindLocalLowBorder(root)
    root.h = reductionMatrixH
    var leftDelEl = findMaxShtraf(root)
    var rightDelEl = leftDelEl
    root.left = Node(matrix.copyAll(), reductionMatrixH+leftDelEl[2], true)
    priorityQueue.add(root.left)
    matrix[rightDelEl[1]][rightDelEl[0]] = m
    root.right = Node(
        matrix = matrix,
        h = reductionMatrixAndFindLocalLowBorder(root),
        isExcluded = false,
        rowExcleded = root.rowExcleded.copyOf().apply { this[rightDelEl[0]] = true },
        columnExcleded = root.columnExcleded. copyOf().apply { this[rightDelEl[1]] = true },
        pointArr = listOf(Pair(rightDelEl[0], rightDelEl[1]))
    )
    priorityQueue.add(root.right)
    var curr: Node
    while (true) {
        curr = priorityQueue.poll()
        if(curr.isExcluded == true) {
            curr.matrix[leftDelEl[0]][leftDelEl[1]] = m
            reductionMatrixH = reductionMatrixAndFindLocalLowBorder(curr)
            leftDelEl = findMaxShtraf(curr)
            curr.left = Node(
                matrix = curr.matrix.copyAll(),
                h = reductionMatrixH,
                isExcluded = true,
                rowExcleded = curr.rowExcleded,
                columnExcleded = curr.columnExcleded,
                pointArr = curr.pointArr
            )
            priorityQueue.add(curr.left)
            curr.matrix[leftDelEl[1]][leftDelEl[0]] = m
            curr.right = Node(
                matrix = curr.matrix.copyAll(),
                h = reductionMatrixAndFindLocalLowBorder(curr),
                isExcluded = false,
                rowExcleded = curr.rowExcleded.copyOf().apply { this[leftDelEl[0]] = true },
                columnExcleded = curr.columnExcleded.copyOf().apply { this[leftDelEl[1]] = true },
                pointArr = curr.pointArr + Pair(leftDelEl[0], leftDelEl[1])
            )
            if (isThisEnd(curr.right!!)) break
            priorityQueue.add(curr.right)
        }else {
            rightDelEl = findMaxShtraf(curr)
            curr.left = Node(curr.matrix.copyAll(), curr.h + rightDelEl[2], true, pointArr = curr.pointArr)
            priorityQueue.add(curr.left)
            curr.matrix[rightDelEl[1]][rightDelEl[0]] = m
            curr.right = Node(
                matrix = curr.matrix.copyAll(),
                h = reductionMatrixAndFindLocalLowBorder(curr),
                isExcluded = false,
                rowExcleded = curr.rowExcleded.copyOf().apply { this[rightDelEl[0]] = true },
                columnExcleded = curr.columnExcleded.copyOf().apply { this[rightDelEl[1]] = true },
                pointArr = curr.pointArr + Pair(rightDelEl[0], rightDelEl[1])
            )
            if (isThisEnd(curr.right!!)) break
            priorityQueue.add(curr.right)
        }
    }
    curr.right!!.pointArr.forEach { println(it) }

    return curr.h

     */
    val n = matrix.size
    val priorityQueue = PriorityQueue<Node>()

    // Начальная редукция корня
    val rootMatrix = matrix.copyAll()
    val initialH = reductionMatrixAndFindLocalLowBorder(rootMatrix, BooleanArray(n), BooleanArray(n))
    val root = Node(rootMatrix, initialH)
    priorityQueue.add(root)

    while (priorityQueue.isNotEmpty()) {
        val curr = priorityQueue.poll()

        // Если все строки вычеркнуты, кроме одной - это финиш
        if (curr.rowExcluded.count { !it } == 1) {
            val lastI = curr.rowExcluded.indexOf(false)
            val lastJ = curr.columnExcluded.indexOf(false)
            curr.pointArr += Pair(lastI, lastJ)
            return curr
        }

        val delEl = findMaxShtraf(curr)
        val i = delEl[0]
        val j = delEl[1]

        // Включаем ребро i -> j
        val leftMatrix = curr.matrix.copyAll()
        // Блокируем обратный путь. Тут блокируется именно не обратный путь matrix[j][i], а путь из j в начало уже найденного пути
        excludPath(leftMatrix, curr.pointArr, i, j)

        val leftRowEx = curr.rowExcluded.copyOf().apply { this[i] = true }
        val leftColEx = curr.columnExcluded.copyOf().apply { this[j] = true }
        // тут редуцируется матрица пришедшая из предыдущего узла. Но с учётом вычеркнутых строк и столбцов
        val leftH = curr.h + reductionMatrixAndFindLocalLowBorder(leftMatrix, leftRowEx, leftColEx)

        priorityQueue.add(Node(leftMatrix, leftH, leftRowEx, leftColEx, curr.pointArr + Pair(i, j)))

        // Исключаем ребро i -> j
        val rightMatrix = curr.matrix.copyAll()
        rightMatrix[i][j] = 1000000
        //Тут редуцируется матрица пришедшая из предыдущего узла, но мы не вычеркивали ничего, только заменили одну клетку на m
        val rightH = curr.h + reductionMatrixAndFindLocalLowBorder(rightMatrix, curr.rowExcluded, curr.columnExcluded)

        priorityQueue.add(Node(rightMatrix, rightH, curr.rowExcluded.copyOf(), curr.columnExcluded.copyOf(), curr.pointArr))

        // Конец шага, мы создали два новых узла, обоим посчитали h и обим редуцировали мтарицы
    }
    return root

}

fun excludPath(matrix: Array<IntArray>, path: List<Pair<Int, Int>>, nextI: Int, nextJ: Int) {
    var start = nextI
    var end = nextJ

    var prevEdge = path.find { it.second == start }
    while (prevEdge != null) {
        start = prevEdge.first // Сдвигаем start еще левее
        prevEdge = path.find { it.second == start } // Ищем нет ли еще кого-то перед ним
    }

    var nextEdge = path.find { it.first == end }
    while (nextEdge != null) {
        end = nextEdge.second // Сдвигаем end еще правее
        nextEdge = path.find { it.first == end } // Ищем нет ли продолжения
    }

    matrix[end][start] = 1000000
}

fun Array<IntArray>.copyAll() = map { it.clone() }.toTypedArray()

fun reductionMatrixAndFindLocalLowBorder(matrix: Array<IntArray>, rowEx: BooleanArray, colEx: BooleanArray): Int {
    val n = matrix.size
    var sum = 0

    /*
    Тут ищу по строке минимум -> удаляю из каждого активного элемента строки этот
    минимум -> прибавляю к общей сумме этот минимум, то есть нахожу h
     */
    for (i in 0..<n) {
        if (rowEx[i]) continue
        var min = m
        //В этом цикле ищу минимум строки
        for(j in 0..<n) {
            if (colEx[j]) continue
            if(matrix[i][j] < min) min = matrix[i][j]
        }
        // Если min == 1_000_000, то делать редукцию нельзя, ведь тогда из всех m вычтется 1_000_000 и они станут равны 0
        // и они снова смогут участвовать в расчёте пути
        if(min > 0 && min < m ) {
            sum += min
            for (j in 0..<n) {
                // Мы вычитаем min только из тех элементов, которые не входят в вычеркнутые столбцы(мы ведь сейчас работаем со строками)
                // и не являются путями равными m
                if(!colEx[j] && matrix[i][j] < m){
                    matrix[i][j] -= min
                }
            }
        }
    }

    /*
    Тут ищу по столбцу минимум -> удаляю из каждого активного элемента столбца этот
    минимум -> прибавляю к общей сумме этот минимум, то есть нахожу h
    */
    for (j in 0..<n) {
        if (colEx[j]) continue
        var min = m
        for(i in 0..<n) {
            if (rowEx[i]) continue
            if(matrix[i][j] < min) min = matrix[i][j]
        }
        if(min > 0 && min < m) {
            sum += min
            for (i in 0..<n) {
                if(!rowEx[i] && matrix[i][j] < m){
                    matrix[i][j] -= min
                }
            }
        }
    }

    return sum
}

fun findMaxShtraf(node: Node) : IntArray {
    var maxShtraf = 0
    var currentShtraf: Int
    var findI = 0
    var findJ = 0
    for(i in node.matrix.indices) {
        if (node.rowExcluded[i]) continue
        for(j in node.matrix[i].indices) {
            if (node.columnExcluded[j]) continue
            if(node.matrix[i][j] == 0) {
                currentShtraf = findShtraf(node, i, j)
                if(currentShtraf > maxShtraf) {
                    maxShtraf = currentShtraf
                    findI = i
                    findJ = j
                }
            }
        }
    }
    return intArrayOf(findI, findJ, maxShtraf)
}

fun findShtraf (node: Node, i: Int, j: Int): Int {

    var minInRow = m
    for(col in node.matrix.indices) {
        //Если элемент строки не равен элементу строки для которого ищется штраф
        // или элемент строки не вычеркнут, то мы смотрим является ли он минимумом по строке
        if(col == j || node.columnExcluded[col]) continue
        if(node.matrix[i][col] < minInRow) minInRow = node.matrix[i][col]
    }

    var minInColumn = m
    for (row in node.matrix.indices){
        //Если элемент столбца не равен элементу столбца для которого ищется штраф
        // или элемент столбца не вычеркнут, то мы смотрим является ли он минимумом по столбцу
        if(row == i || node.rowExcluded[row]) continue
        if(node.matrix[row][j] < minInColumn) minInColumn = node.matrix[row][j]
    }

    return minInRow + minInColumn
}

fun isThisEnd (node: Node): Boolean {
    var count = 0
    for (value in node.rowExcluded) {
        if (value == false) count++
    }
    if (count == 1) {
        node.pointArr += Pair(node.rowExcluded.indexOf(false), node.columnExcluded.indexOf(false))
    }
    return count == 1
}


data class Node  (
    val matrix: Array<IntArray>,
    var h: Int,
    var rowExcluded: BooleanArray = BooleanArray(matrix.size) {false},
    var columnExcluded: BooleanArray = BooleanArray(matrix.size) {false},
    var pointArr: List<Pair<Int, Int>> = emptyList()
) : Comparable<Node> {
    override fun compareTo(other: Node): Int {
        return compareValuesBy(this, other, Node::h)
    }
}
