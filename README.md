# Lab10 DFS 與 Beeper 判斷策略整理

## 1. Lab10_6：DFS Coordinate Tracking Approach

在 `Lab10_6` 中，使用 **DFS（Depth-First Search，深度優先搜尋）** 搭配 **座標追蹤** 的方式，避免機器人重複探索已經走過的路徑。

### 核心概念

當機器人移動到新的格子時，會將目前的位置座標記錄到一個集合中：

```text
visited
```

這個 `visited` set 用來儲存所有已經走過的座標。

### 執行流程

1. 機器人移動到一個新的 cell。
2. 將目前座標記錄進 `visited` set。
3. 在 DFS 遞迴探索下一個 cell 之前，先檢查目標座標是否已經存在於 `visited` 中。
4. 如果該座標已經被拜訪過，代表機器人之前已經走過這個位置。
5. 因此不再繼續探索該路徑，而是立即 **backtrack**。

### 目的

這樣可以避免機器人陷入重複繞圈的情況，提升搜尋迷宮出口或目標 beeper 的效率。

---

## 2. Lab10_6_1：Single Beeper Ambiguity Problem

在 `Lab10_6_1` 中，當機器人走到某個 cell，並發現地上有 **一個 beeper** 時，會遇到判斷上的問題。

### 問題描述

機器人無法直接分辨這個 beeper 是哪一種情況：

| 情況 | 說明 |
|---|---|
| Actual Goal | 這個 beeper 是地圖一開始就放好的真正目標 |
| Own Marker | 這個 beeper 是機器人之前走過時留下的標記 |

### 為什麼會造成問題？

因為這兩種情況在機器人的視角中看起來完全一樣：

```text
There is one beeper on the ground.
```

機器人只能知道目前 cell 有一個 beeper，卻無法知道這個 beeper 的來源。

### 造成的困難

機器人無法判斷自己應該：

- 宣告成功，表示已經抵達真正的 goal
- 還是把這裡當成之前走過的路徑，視為 dead end 並 backtrack

因此，單純依靠「地上是否有一個 beeper」並不足以可靠判斷是否抵達目標。

---

## 3. Lab10_6_2：Using Isolated Goal Beeper Property

在 `Lab10_6_2` 中，可以利用 goal beeper 的特性來解決 `Lab10_6_1` 的 ambiguity problem。

### 核心觀察

真正的 goal beeper 通常是 **isolated** 的，也就是它周圍沒有其他相鄰的 beeper。

相對地，機器人留下的 marker 通常會形成一條 trail：

```text
beeper - beeper - beeper - beeper
```

因此，marker beepers 通常會和其他 beepers 相鄰。

### 判斷策略

當機器人發現目前 cell 有一個 beeper 時，不要立刻判斷為 goal。

應該進一步檢查附近是否有相鄰的 beepers：

| 判斷結果 | 意義 |
|---|---|
| No neighboring beepers | 可能是真正的 goal |
| Has neighboring beepers | 可能是機器人之前留下的 trail marker |

### 目的

透過「goal beeper 通常是孤立的」這個特性，機器人可以更合理地區分：

- 真正的終點 beeper
- 因為繞圈而再次遇到的自身 marker

---

## 4. Lab 對應 Extension 整理

| Lab | 對應 Extension | 說明 |
|---|---|---|
| `Lab10_1` | `Extension2` | 使用原本的 right-hand wall-following 或相關迷宮搜尋邏輯 |
| `Lab10_6` | `Extension3` | 使用 DFS 與座標追蹤避免重複拜訪 |
| `Lab10_6_1` | `Extension3` | 說明 single beeper 導致的目標判斷模糊問題 |
| `Lab10_6_2` | `Extension3` | 使用 isolated goal beeper 的特性改善判斷 |

---

## 5. Summary

`Lab10_6` 系列的重點在於改善機器人在迷宮中的搜尋能力。

- `Lab10_6` 使用 DFS 與 `visited` set 避免重複探索。
- `Lab10_6_1` 指出單一 beeper 會造成 goal 與 marker 無法分辨的問題。
- `Lab10_6_2` 則利用 goal beeper 通常是 isolated 的特性，協助機器人判斷是否真正抵達終點。
- `Lab10_1` 對應 `Extension2`，而 `Lab10_6`、`Lab10_6_1`、`Lab10_6_2` 都對應 `Extension3`。
