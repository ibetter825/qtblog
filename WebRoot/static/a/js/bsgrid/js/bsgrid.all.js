/**
* jQuery.bsgrid v1.37 by @Baishui2004
* Copyright 2014 Apache v2 License
* https://github.com/baishui2004/jquery.bsgrid
*/

String.prototype.startWith = function(a) {
    if (a == null || a == "" || this.length == 0 || a.length > this.length) {
        return false
    } else {
        return this.substr(0, a.length) == a
    }
};
String.prototype.endWith = function(a) {
    if (a == null || a == "" || this.length == 0 || a.length > this.length) {
        return false
    } else {
        return this.substring(this.length - a.length) == a
    }
};
String.prototype.replaceAll = function(a, b) {
    return this.replace(new RegExp(a, "gm"), b)
};
function StringBuilder() {
    if (arguments.length) {
        this.append.apply(this, arguments)
    }
}
StringBuilder.prototype = function() {
    var c = Array.prototype.join,
    d = Array.prototype.slice,
    a = /\{(\d+)\}/g,
    b = function() {
        return c.call(this, "")
    };
    return {
        constructor: StringBuilder,
        length: 0,
        append: Array.prototype.push,
        appendFormat: function(e) {
            var g = 0,
            f = d.call(arguments, 1);
            this.append(a.test(e) ? e.replace(a,
            function(h, j) {
                return f[j]
            }) : e.replace(/\?/g,
            function() {
                return f[g++]
            }));
            return this
        },
        size: function() {
            return this.toString().length
        },
        toString: b,
        valueOf: b
    }
} ();
$.bsgrid = {
    param: function(c, b) {
        if (b == undefined) {
            b = false
        }
        if (!b) {
            return $.param(c)
        }
        var d = new StringBuilder();
        if (c instanceof Array) {
            $.each(c,
            function(f, e) {
                d.append("&" + e.name + "=");
                d.append(encodeURIComponent(encodeURIComponent(e.value)))
            })
        } else {
            for (var a in c) {
                d.append("&" + a + "=");
                d.append(encodeURIComponent(encodeURIComponent(c[a])))
            }
        }
        return d.length > 0 ? d.toString().substring(1) : ""
    },
    getKeysString: function(c, a) {
        if (a == undefined) {
            a = ","
        }
        var d = new StringBuilder();
        if (c instanceof Array) {
            $.each(c,
            function(f, e) {
                if ((d.toString() + a).indexOf(a + e.name + a) == -1) {
                    d.append(a + e.name)
                }
            })
        } else {
            for (var b in c) {
                d.append(a + b)
            }
        }
        return d.length > 0 ? d.toString().substring(1) : ""
    },
    forcePushPropertyInObject: function(b, a, c) {
        if (b.hasOwnProperty(a)) {
            b[a + "_f"] = c
        } else {
            b[a] = c
        }
    },
    adaptAttrOrProp: function(b, d, c) {
        var a = parseInt($.fn.jquery.substring(0, $.fn.jquery.indexOf(".", 2)).replace(".", ""));
        if (c == undefined) {
            if (a >= 16) {
                return b.prop(d)
            } else {
                return b.attr(d)
            }
        } else {
            if (a >= 16) {
                b.prop(d, c)
            } else {
                b.attr(d, c)
            }
        }
    },
    alert: function(a) {
        alert(a)
    }
}; (function(a) {
    a.fn.bsgrid_paging = {
        defaults: {
            loopback: false,
            pageSize: 20,
            pageSizeSelect: false,
            pageSizeForGrid: [5, 10, 20, 25, 50, 100, 200, 500],
            pageIncorrectTurnAlert: true,
            pagingLittleToolbar: false,
            pagingBtnClass: "pagingBtn",
            pagingMinWidth: "auto",
            pagingBtnShowState: {
                select: true,
                first: true,
                prev: true,
                next: true,
                last: true,
                gotoBtn: true,
                refresh: true
            }
        },
        pagingObjs: {},
        init: function(h, g) {
            var d = {
                settings: a.extend(true, {},
                a.fn.bsgrid_paging.defaults, g),
                pagingId: h,
                totalRowsId: h + "_totalRows",
                totalPagesId: h + "_totalPages",
                curPageId: h + "_curPage",
                gotoPageInputId: h + "_gotoPageInput",
                gotoPageId: h + "_gotoPage",
                refreshPageId: h + "_refreshPage",
                pageSizeId: h + "_pageSize",
                firstPageId: h + "_firstPage",
                prevPageId: h + "_prevPage",
                nextPageId: h + "_nextPage",
                lastPageId: h + "_lastPage",
                startRowId: h + "_startRow",
                endRowId: h + "_endRow",
                totalRows: 0,
                totalPages: 0,
                curPage: 1,
                curPageRowsNum: 0,
                startRow: 0,
                endRow: 0
            };
            if (g.pageSizeForGrid != undefined) {
                d.settings.pageSizeForGrid = g.pageSizeForGrid
            }
            var b = {
                options: d,
                page: function(i) {
                    a.fn.bsgrid_paging.page(i, d)
                },
                getCurPage: function() {
                    return a.fn.bsgrid_paging.getCurPage(d)
                },
                refreshPage: function() {
                    a.fn.bsgrid_paging.refreshPage(d)
                },
                firstPage: function() {
                    a.fn.bsgrid_paging.firstPage(d)
                },
                prevPage: function() {
                    a.fn.bsgrid_paging.prevPage(d)
                },
                nextPage: function() {
                    a.fn.bsgrid_paging.nextPage(d)
                },
                lastPage: function() {
                    a.fn.bsgrid_paging.lastPage(d)
                },
                gotoPage: function(i) {
                    a.fn.bsgrid_paging.gotoPage(d, i)
                },
                createPagingToolbar: function() {
                    return a.fn.bsgrid_paging.createPagingToolbar(d)
                },
                setPagingToolbarEvents: function() {
                    a.fn.bsgrid_paging.setPagingToolbarEvents(d)
                },
                dynamicChangePagingButtonStyle: function() {
                    a.fn.bsgrid_paging.dynamicChangePagingButtonStyle(d)
                },
                setPagingValues: function(j, i) {
                    a.fn.bsgrid_paging.setPagingValues(j, i, d)
                }
            };
            a.fn.bsgrid_paging.pagingObjs[h] = b;
            a("#" + h).append(b.createPagingToolbar());
            if (d.settings.pageSizeSelect) {
                if (a.inArray(d.settings.pageSize, d.settings.pageSizeForGrid) == -1) {
                    d.settings.pageSizeForGrid.push(d.settings.pageSize)
                }
                d.settings.pageSizeForGrid.sort(function(j, i) {
                    return j - i
                });
                var f = new StringBuilder();
                for (var e = 0; e < d.settings.pageSizeForGrid.length; e++) {
                    var c = d.settings.pageSizeForGrid[e];
                    f.append('<option value="' + c + '">' + c + "</option>")
                }
                a("#" + d.pageSizeId).html(f.toString()).val(d.settings.pageSize)
            }
            b.setPagingToolbarEvents();
            return b
        },
        getPagingObj: function(c) {
            var b = a.fn.bsgrid_paging.pagingObjs[c];
            return b ? b: null
        },
        page: function(c, b) {
            var d = a.fn.bsgrid.getGridObj(b.settings.gridId);
            d.options.settings.pageSize = b.settings.pageSize;
            a.fn.bsgrid.page(c, d.options)
        },
        getCurPage: function(b) {
            var c = a("#" + b.curPageId).html();
            return c == "" ? 1 : c
        },
        refreshPage: function(b) {
            a.fn.bsgrid_paging.page(a.fn.bsgrid_paging.getCurPage(b), b)
        },
        firstPage: function(b) {
            var c = a.fn.bsgrid_paging.getCurPage(b);
            if (c <= 1) {
                a.fn.bsgrid_paging.incorrectTurnAlert(b, a.bsgridLanguage.isFirstPage);
                return
            }
            a.fn.bsgrid_paging.page(1, b)
        },
        prevPage: function(b) {
            var c = a.fn.bsgrid_paging.getCurPage(b);
            if (c <= 1) {
                if (b.settings.loopback && b.totalPages > 0) {
                    a.fn.bsgrid_paging.page(b.totalPages, b);
                    return
                } else {
                    a.fn.bsgrid_paging.incorrectTurnAlert(b, a.bsgridLanguage.isFirstPage);
                    return
                }
            }
            a.fn.bsgrid_paging.page(parseInt(c) - 1, b)
        },
        nextPage: function(b) {
            var c = a.fn.bsgrid_paging.getCurPage(b);
            if (c >= b.totalPages) {
                if (b.settings.loopback && c > 0) {
                    a.fn.bsgrid_paging.page(1, b);
                    return
                } else {
                    a.fn.bsgrid_paging.incorrectTurnAlert(b, a.bsgridLanguage.isLastPage);
                    return
                }
            }
            a.fn.bsgrid_paging.page(parseInt(c) + 1, b)
        },
        lastPage: function(b) {
            var c = a.fn.bsgrid_paging.getCurPage(b);
            if (c >= b.totalPages) {
                a.fn.bsgrid_paging.incorrectTurnAlert(b, a.bsgridLanguage.isLastPage);
                return
            }
            a.fn.bsgrid_paging.page(b.totalPages, b)
        },
        gotoPage: function(b, c) {
            if (c == undefined) {
                c = a("#" + b.gotoPageInputId).val()
            }
            if (a.trim(c) == "" || isNaN(c)) {
                a.fn.bsgrid_paging.alert(a.bsgridLanguage.needInteger)
            } else {
                if (parseInt(c) < 1 || parseInt(c) > b.totalPages) {
                    a.fn.bsgrid_paging.alert(a.bsgridLanguage.needRange(1, b.totalPages))
                } else {
                    a("#" + b.gotoPageInputId).val(c);
                    a.fn.bsgrid_paging.page(parseInt(c), b)
                }
            }
        },
        incorrectTurnAlert: function(b, c) {
            if (b.settings.pageIncorrectTurnAlert) {
                a.fn.bsgrid_paging.alert(c)
            }
        },
        alert: function(c) {
            try {
                a.bsgrid.alert(c)
            } catch(b) {
                alert(c)
            }
        },
        createPagingToolbar: function(b) {
            var e = new StringBuilder();
            var c = b.settings.pagingLittleToolbar;
            e.append('<table class="bsgridPaging' + (c ? " pagingLittleToolbar": "") + (b.settings.pageSizeSelect ? "": " noPageSizeSelect") + '"');
            if (b.settings.pagingMinWidth != "auto") {
                e.append(' style="width: ' + b.settings.pagingMinWidth + ' !important"')
            }
            e.append(">");
            e.append("<tr>");
            var f = b.settings.pagingBtnShowState;
            if (b.settings.pageSizeSelect && f.select) {
                e.append("<td>" + a.bsgridLanguage.pagingToolbar.pageSizeDisplay(b.pageSizeId, c) + "</td>")
            }
            e.append("<td>" + a.bsgridLanguage.pagingToolbar.currentDisplayRows(b.startRowId, b.endRowId, c) + "</td>");
            e.append("<td>" + a.bsgridLanguage.pagingToolbar.totalRows(b.totalRowsId) + "</td>");
            var d = b.settings.pagingBtnClass;
            e.append("<td>");
            if (f.first) {
                e.append('<input class="' + d + ' firstPage" type="button" id="' + b.firstPageId + '" value="' + (c ? "": a.bsgridLanguage.pagingToolbar.firstPage) + '" />')
            }
            if (f.first && f.prev) {
                e.append("&nbsp;")
            }
            if (f.prev) {
                e.append('<input class="' + d + ' prevPage" type="button" id="' + b.prevPageId + '" value="' + (c ? "": a.bsgridLanguage.pagingToolbar.prevPage) + '" />')
            }
            e.append("</td>");
            e.append("<td>" + a.bsgridLanguage.pagingToolbar.currentDisplayPageAndTotalPages(b.curPageId, b.totalPagesId) + "</td>");
            e.append("<td>");
            if (f.next) {
                e.append('<input class="' + d + ' nextPage" type="button" id="' + b.nextPageId + '" value="' + (c ? "": a.bsgridLanguage.pagingToolbar.nextPage) + '" />')
            }
            if (f.next && f.last) {
                e.append("&nbsp;")
            }
            if (f.last) {
                e.append('<input class="' + d + ' lastPage" type="button" id="' + b.lastPageId + '" value="' + (c ? "": a.bsgridLanguage.pagingToolbar.lastPage) + '" />')
            }
            e.append("</td>");
            if (f.gotoBtn) {
                e.append('<td class="gotoPageInputTd">');
                e.append('<input class="gotoPageInput" type="text" id="' + b.gotoPageInputId + '" />');
                e.append("</td>");
                e.append('<td class="gotoPageButtonTd">');
                e.append('<input class="' + d + ' gotoPage" type="button" id="' + b.gotoPageId + '" value="' + (c ? "": a.bsgridLanguage.pagingToolbar.gotoPage) + '" />');
                e.append("</td>")
            }
            if (f.refresh) {
                e.append('<td class="refreshPageTd">');
                e.append('<input class="' + d + ' refreshPage" type="button" id="' + b.refreshPageId + '" value="' + (c ? "": a.bsgridLanguage.pagingToolbar.refreshPage) + '" />');
                e.append("</td>")
            }
            e.append("</tr>");
            e.append("</table>");
            return e.toString()
        },
        setPagingToolbarEvents: function(b) {
            if (b.settings.pageSizeSelect) {
                a("#" + b.pageSizeId).change(function() {
                    b.settings.pageSize = parseInt(a(this).val());
                    a(this).trigger("blur");
                    a.fn.bsgrid_paging.page(1, b)
                })
            }
            a("#" + b.firstPageId).click(function() {
                a.fn.bsgrid_paging.firstPage(b)
            });
            a("#" + b.prevPageId).click(function() {
                a.fn.bsgrid_paging.prevPage(b)
            });
            a("#" + b.nextPageId).click(function() {
                a.fn.bsgrid_paging.nextPage(b)
            });
            a("#" + b.lastPageId).click(function() {
                a.fn.bsgrid_paging.lastPage(b)
            });
            a("#" + b.gotoPageInputId).keyup(function(c) {
                if (c.which == 13) {
                    a.fn.bsgrid_paging.gotoPage(b)
                }
            });
            a("#" + b.gotoPageId).click(function() {
                a.fn.bsgrid_paging.gotoPage(b)
            });
            a("#" + b.refreshPageId).click(function() {
                a.fn.bsgrid_paging.refreshPage(b)
            })
        },
        dynamicChangePagingButtonStyle: function(b) {
            var c = "disabledCls";
            if (b.curPage <= 1) {
                a("#" + b.firstPageId).addClass(c);
                a("#" + b.prevPageId).addClass(c)
            } else {
                a("#" + b.firstPageId).removeClass(c);
                a("#" + b.prevPageId).removeClass(c)
            }
            if (b.curPage >= b.totalPages) {
                a("#" + b.nextPageId).addClass(c);
                a("#" + b.lastPageId).addClass(c)
            } else {
                a("#" + b.nextPageId).removeClass(c);
                a("#" + b.lastPageId).removeClass(c)
            }
        },
        setPagingValues: function(i, g, f) {
            i = Math.max(i, 1);
            var b = f.settings.pageSize;
            var h = parseInt(g / b);
            h = parseInt((g % b == 0) ? h: h + 1);
            var e = (i * b < g) ? b: (g - (i - 1) * b);
            var d = (i - 1) * b + 1;
            var c = d + e - 1;
            d = e <= 0 ? 0 : d;
            c = e <= 0 ? 0 : c;
            f.totalRows = g;
            f.totalPages = h;
            f.curPage = i;
            f.curPageRowsNum = e;
            f.startRow = d;
            f.endRow = c;
            a("#" + f.totalRowsId).html(f.totalRows);
            a("#" + f.totalPagesId).html(f.totalPages);
            a("#" + f.curPageId).html(f.curPage);
            a("#" + f.startRowId).html(f.startRow);
            a("#" + f.endRowId).html(f.endRow);
            a.fn.bsgrid_paging.dynamicChangePagingButtonStyle(f)
        }
    }
})(jQuery); (function($) {
    $.fn.bsgrid = {
        version: "1.37",
        defaults: {
            dataType: "json",
            ajaxType: "post",
            localData: false,
            url: "",
            otherParames: false,
            autoLoad: true,
            pageAll: false,
            showPageToolbar: true,
            pageSize: 20,
            pageSizeSelect: false,
            pageSizeForGrid: [5, 10, 20, 25, 50, 100, 200, 500],
            pageIncorrectTurnAlert: true,
            multiSort: false,
            displayBlankRows: true,
            lineWrap: false,
            stripeRows: false,
            rowHoverColor: false,
            rowSelectedColor: true,
            pagingLittleToolbar: false,
            pagingToolbarAlign: "right",
            pagingBtnClass: "pagingBtn",
            displayPagingToolbarOnlyMultiPages: false,
            isProcessLockScreen: true,
            longLengthAotoSubAndTip: true,
            colsProperties: {
                align: "center",
                maxLength: 40,
                indexAttr: "w_index",
                sortAttr: "w_sort",
                alignAttr: "w_align",
                lengthAttr: "w_length",
                renderAttr: "w_render",
                hiddenAttr: "w_hidden",
                tipAttr: "w_tip"
            },
            requestParamsName: {
                pageSize: "pageSize",
                curPage: "curPage",
                sortName: "sortName",
                sortOrder: "sortOrder"
            },
            beforeSend: function(options, XMLHttpRequest) {},
            complete: function(options, XMLHttpRequest, textStatus) {},
            processUserdata: function(userdata, options) {},
            event: {
                selectRowEvent: false,
                unselectRowEvent: false,
                customRowEvents: {},
                customCellEvents: {}
            },
            extend: {
                initGridMethods: {},
                beforeRenderGridMethods: {},
                renderPerRowMethods: {},
                renderPerColumnMethods: {},
                afterRenderGridMethods: {}
            },
            additionalBeforeRenderGrid: function(parseSuccess, gridData, options) {},
            additionalRenderPerRow: function(record, rowIndex, trObj, options) {},
            additionalRenderPerColumn: function(record, rowIndex, colIndex, tdObj, trObj, options) {},
            additionalAfterRenderGrid: function(parseSuccess, gridData, options) {}
        },
        gridObjs: {},
        init: function(gridId, settings) {
            if (!$("#" + gridId).hasClass("bsgrid")) {
                $("#" + gridId).addClass("bsgrid")
            }
            var options = {
                settings: $.extend(true, {},
                $.fn.bsgrid.defaults, settings),
                gridId: gridId,
                noPagingationId: gridId + "_no_pagination",
                pagingOutTabId: gridId + "_pt_outTab",
                pagingId: gridId + "_pt",
                sortName: "",
                sortOrder: "",
                otherParames: settings.otherParames,
                totalRows: 0,
                totalPages: 0,
                curPage: 1,
                curPageRowsNum: 0,
                startRow: 0,
                endRow: 0
            };
            if ($("#" + gridId).find("thead").length == 0) {
                $("#" + gridId).prepend("<thead></thead>");
                $("#" + gridId).find("tr:lt(" + ($("#" + gridId + " tr").length - $("#" + gridId + " tfoot tr").length) + ")").appendTo($("#" + gridId + " thead"))
            }
            if ($("#" + gridId).find("tbody").length == 0) {
                $("#" + gridId + " thead").after("<tbody></tbody>")
            }
            if ($("#" + gridId).find("tfoot").length == 0) {
                $("#" + gridId).append('<tfoot style="display: none;"></tfoot>')
            }
            options.columnsModel = $.fn.bsgrid.initColumnsModel(options);
            if (settings.pageSizeForGrid != undefined) {
                options.settings.pageSizeForGrid = settings.pageSizeForGrid
            }
            options.settings.dataType = options.settings.dataType.toLowerCase();
            if (options.settings.pageSizeSelect) {
                if ($.inArray(options.settings.pageSize, options.settings.pageSizeForGrid) == -1) {
                    options.settings.pageSizeForGrid.push(options.settings.pageSize)
                }
                options.settings.pageSizeForGrid.sort(function(a, b) {
                    return a - b
                })
            }
            var gridObj = {
                options: options,
                getPageCondition: function(curPage) {
                    return $.fn.bsgrid.getPageCondition(curPage, options)
                },
                page: function(curPage) {
                    $.fn.bsgrid.page(curPage, options)
                },
                search: function(params) {
                    $.fn.bsgrid.search(params, options)
                },
                loadGridData: function(dataType, gridData) {
                    $.fn.bsgrid.loadGridData(dataType, gridData, options)
                },
                reloadLocalData: function(localData) {
                    $.fn.bsgrid.reloadLocalData(localData, options)
                },
                getPageSize: function() {
                    return options.settings.pageSize
                },
                getTotalRows: function() {
                    return options.totalRows
                },
                getTotalPages: function() {
                    return options.totalPages
                },
                getCurPage: function() {
                    return options.curPage
                },
                getCurPageRowsNum: function() {
                    return options.curPageRowsNum
                },
                getStartRow: function() {
                    return options.startRow
                },
                getEndRow: function() {
                    return options.endRow
                },
                getSortName: function() {
                    return options.sortName
                },
                getSortOrder: function() {
                    return options.sortOrder
                },
                getRows: function() {
                    return $.fn.bsgrid.getRows(options)
                },
                getRow: function(row) {
                    return $.fn.bsgrid.getRow(row, options)
                },
                getRowCells: function(row) {
                    return $.fn.bsgrid.getRowCells(row, options)
                },
                getColCells: function(col) {
                    return $.fn.bsgrid.getColCells(col, options)
                },
                getCell: function(row, col) {
                    return $.fn.bsgrid.getCell(row, col, options)
                },
                getSelectedRow: function() {
                    return $.fn.bsgrid.getSelectedRow(options)
                },
                getSelectedRowIndex: function() {
                    return $.fn.bsgrid.getSelectedRowIndex(options)
                },
                selectRow: function(row) {
                    return $.fn.bsgrid.selectRow(row, options)
                },
                unSelectRow: function() {
                    return $.fn.bsgrid.unSelectRow(options)
                },
                getUserdata: function() {
                    return $.fn.bsgrid.getUserdata(options)
                },
                getRowRecord: function(rowObj) {
                    return $.fn.bsgrid.getRowRecord(rowObj)
                },
                getAllRecords: function() {
                    return $.fn.bsgrid.getAllRecords(options)
                },
                getRecord: function(row) {
                    return $.fn.bsgrid.getRecord(row, options)
                },
                getRecordIndexValue: function(record, index) {
                    return $.fn.bsgrid.getRecordIndexValue(record, index, options)
                },
                getColumnValue: function(row, index) {
                    return $.fn.bsgrid.getColumnValue(row, index, options)
                },
                getCellRecordValue: function(row, col) {
                    return $.fn.bsgrid.getCellRecordValue(row, col, options)
                },
                sort: function(obj) {
                    $.fn.bsgrid.sort(obj, options)
                },
                getGridHeaderObject: function() {
                    return $.fn.bsgrid.getGridHeaderObject(options)
                },
                getColumnModel: function(colIndex) {
                    return $.fn.bsgrid.getColumnModel(colIndex, options)
                },
                appendHeaderSort: function() {
                    $.fn.bsgrid.appendHeaderSort(options)
                },
                setGridBlankBody: function() {
                    $.fn.bsgrid.setGridBlankBody(options)
                },
                createPagingOutTab: function() {
                    $.fn.bsgrid.createPagingOutTab(options)
                },
                clearGridBodyData: function() {
                    $.fn.bsgrid.clearGridBodyData(options)
                },
                getPagingObj: function() {
                    return $.fn.bsgrid.getPagingObj(options)
                },
                getCurPage: function() {
                    return $.fn.bsgrid.getCurPage(options)
                },
                refreshPage: function() {
                    $.fn.bsgrid.refreshPage(options)
                },
                firstPage: function() {
                    $.fn.bsgrid.firstPage(options)
                },
                prevPage: function() {
                    $.fn.bsgrid.prevPage(options)
                },
                nextPage: function() {
                    $.fn.bsgrid.nextPage(options)
                },
                lastPage: function() {
                    $.fn.bsgrid.lastPage(options)
                },
                gotoPage: function(goPage) {
                    $.fn.bsgrid.gotoPage(options, goPage)
                },
                initPaging: function() {
                    return $.fn.bsgrid.initPaging(options)
                },
                setPagingValues: function() {
                    $.fn.bsgrid.setPagingValues(options)
                }
            };
            $.fn.bsgrid.gridObjs[gridId] = gridObj;
            if (options.settings.pageAll || options.settings.pageSize < 1) {
                options.settings.pageAll = true;
                options.settings.pageSize = 0
            }
            gridObj.appendHeaderSort();
            gridObj.createPagingOutTab();
            if (!options.settings.showPageToolbar) {
                $("#" + options.pagingId).hide();
                $("#" + options.pagingOutTabId).hide()
            }
            if (!options.settings.pageAll) {
                gridObj.pagingObj = gridObj.initPaging();
                try {
                    var minWidth = $.trim($("#" + options.pagingId).children().width());
                    minWidth = minWidth == "" ? 0 : parseInt(minWidth);
                    if (minWidth != 0) {
                        $("#" + gridId).css("min-width", minWidth + 16);
                        $("#" + options.pagingOutTabId).css("min-width", minWidth + 16)
                    }
                    $("#" + options.pagingOutTabId).width($("#" + gridId).width());
                    $(window).resize(function() {
                        $("#" + options.pagingOutTabId).width($("#" + gridId).width())
                    })
                } catch(e) {}
            }
            if (options.settings.isProcessLockScreen) {
                $.fn.bsgrid.addLockScreen(options)
            }
            try {
                $.fn.bsgrid.extendInitGrid.initGridExtendOptions(gridId, options)
            } catch(e) {}
            for (var key in options.settings.extend.initGridMethods) {
                options.settings.extend.initGridMethods[key](gridId, options)
            }
            if (options.settings.autoLoad) {
                setTimeout(function() {
                    gridObj.page(1)
                },
                10)
            } else {
                gridObj.setGridBlankBody()
            }
            return gridObj
        },
        initColumnsModel: function(options) {
            var columnsModel = [];
            $.fn.bsgrid.getGridHeaderObject(options).each(function() {
                var colsProperties = options.settings.colsProperties;
                var columnModel = {};
                columnModel.sortName = "";
                columnModel.sortOrder = "";
                var sortInfo = $.trim($(this).attr(colsProperties.sortAttr));
                if (sortInfo.length != 0) {
                    var sortInfoArray = sortInfo.split(",");
                    columnModel.sortName = $.trim(sortInfoArray[0]);
                    columnModel.sortOrder = $.trim(sortInfoArray.length > 1 ? sortInfoArray[1] : "")
                }
                columnModel.index = $.trim($(this).attr(colsProperties.indexAttr));
                columnModel.render = $.trim($(this).attr(colsProperties.renderAttr));
                columnModel.tip = $.trim($(this).attr(colsProperties.tipAttr));
                var maxLen = $.trim($(this).attr(colsProperties.lengthAttr));
                columnModel.maxLen = maxLen.length != 0 ? parseInt(maxLen) : colsProperties.maxLength;
                var align = $.trim($(this).attr(colsProperties.alignAttr));
                columnModel.align = align == "" ? colsProperties.align: align;
                columnModel.hidden = $.trim($(this).attr(colsProperties.hiddenAttr));
                columnsModel.push(columnModel)
            });
            return columnsModel
        },
        getGridObj: function(gridId) {
            var obj = $.fn.bsgrid.gridObjs[gridId];
            return obj ? obj: null
        },
        buildData: {
            gridData: function(type, curPage, data) {
                if (type == "json") {
                    return $.fn.bsgrid.buildJsonData.gridData(curPage, data)
                } else {
                    if (type == "xml") {
                        return $.fn.bsgrid.buildXmlData.gridData(curPage, data)
                    }
                }
                return false
            }
        },
        parseData: {
            success: function(type, gridData) {
                if (type == "json") {
                    return $.fn.bsgrid.parseJsonData.success(gridData)
                } else {
                    if (type == "xml") {
                        return $.fn.bsgrid.parseXmlData.success(gridData)
                    }
                }
                return false
            },
            totalRows: function(type, gridData) {
                if (type == "json") {
                    return $.fn.bsgrid.parseJsonData.totalRows(gridData)
                } else {
                    if (type == "xml") {
                        return $.fn.bsgrid.parseXmlData.totalRows(gridData)
                    }
                }
                return false
            },
            curPage: function(type, gridData) {
                if (type == "json") {
                    return $.fn.bsgrid.parseJsonData.curPage(gridData)
                } else {
                    if (type == "xml") {
                        return $.fn.bsgrid.parseXmlData.curPage(gridData)
                    }
                }
                return false
            },
            data: function(type, gridData) {
                if (type == "json") {
                    return $.fn.bsgrid.parseJsonData.data(gridData)
                } else {
                    if (type == "xml") {
                        return $.fn.bsgrid.parseXmlData.data(gridData)
                    }
                }
                return false
            },
            userdata: function(type, gridData) {
                if (type == "json") {
                    return $.fn.bsgrid.parseJsonData.userdata(gridData)
                } else {
                    if (type == "xml") {
                        return $.fn.bsgrid.parseXmlData.userdata(gridData)
                    }
                }
                return false
            },
            getDataLen: function(type, gridData) {
                if (type == "json" || type == "xml") {
                    return $.fn.bsgrid.parseData.data(type, gridData).length
                }
                return 0
            },
            getRecord: function(type, data, row) {
                if (type == "json") {
                    return $.fn.bsgrid.parseJsonData.getRecord(data, row)
                } else {
                    if (type == "xml") {
                        return $.fn.bsgrid.parseXmlData.getRecord(data, row)
                    }
                }
                return false
            },
            getColumnValue: function(type, record, index) {
                if (type == "json") {
                    return $.fn.bsgrid.parseJsonData.getColumnValue(record, index)
                } else {
                    if (type == "xml") {
                        return $.fn.bsgrid.parseXmlData.getColumnValue(record, index)
                    }
                }
                return false
            }
        },
        buildJsonData: {
            gridData: function(curPage, data) {
                return {
                    success: true,
                    totalRows: data.length,
                    curPage: curPage,
                    data: data
                }
            }
        },
        parseJsonData: {
            success: function(json) {
                return json.success
            },
            totalRows: function(json) {
                return json.totalRows
            },
            curPage: function(json) {
                return json.curPage
            },
            data: function(json) {
                return json.data
            },
            userdata: function(json) {
                return json.userdata
            },
            getRecord: function(data, row) {
                return data[row]
            },
            getColumnValue: function(record, index) {
                return $.trim(record[index])
            }
        },
        buildXmlData: {
            gridData: function(curPage, data) {
                return '<?xml version="1.0" encoding="UTF-8"?><gridData><success>true</success><totalRows>' + $("<xml>" + data + "</xml>").find("row").length + "</totalRows><curPage>" + curPage + "</curPage><data>" + data + "</data></gridData>"
            }
        },
        parseXmlData: {
            success: function(xml) {
                return $.trim($(xml).find("gridData success").text()) == "true"
            },
            totalRows: function(xml) {
                return parseInt($(xml).find("gridData totalRows").text())
            },
            curPage: function(xml) {
                return parseInt($(xml).find("gridData curPage").text())
            },
            data: function(xml) {
                return $(xml).find("gridData data row")
            },
            userdata: function(xml) {
                return $(xml).find("gridData userdata")
            },
            getRecord: function(data, row) {
                return data.eq(row)
            },
            getColumnValue: function(record, index) {
                return $.trim(record.find(index).text())
            }
        },
        getPageCondition: function(curPage, options) {
            var params = new StringBuilder();
            if (options.otherParames == false) {} else {
                if ((typeof options.otherParames).toLowerCase() == "string" || options.otherParames instanceof String) {
                    params.append("&" + options.otherParames)
                } else {
                    if (options.otherParames instanceof Array) {
                        $.each(options.otherParames,
                        function(i, objVal) {
                            params.append("&" + objVal.name + "=" + objVal.value)
                        })
                    } else {
                        for (var key in options.otherParames) {
                            params.append("&" + key + "=" + options.otherParames[key])
                        }
                    }
                }
            }
            var condition = params.length == 0 ? "": params.toString().substring(1);
            condition += (condition.length == 0 ? "": "&") + options.settings.requestParamsName.pageSize + "=" + options.settings.pageSize + "&" + options.settings.requestParamsName.curPage + "=" + curPage + "&" + options.settings.requestParamsName.sortName + "=" + options.sortName + "&" + options.settings.requestParamsName.sortOrder + "=" + options.sortOrder;
            return condition
        },
        search: function(params, options) {
            if (params != undefined) {
                options.otherParames = params
            } else {
                options.otherParames = false
            }
            $.fn.bsgrid.page(1, options)
        },
        page: function(curPage, options) {
            if ($.trim(curPage) == "" || isNaN(curPage)) {
                $.fn.bsgrid.alert($.bsgridLanguage.needInteger);
                return
            }
            var dataType = options.settings.dataType;
            if (options.settings.localData != false) {
                if (dataType == "json") {
                    $.fn.bsgrid.loadGridData(dataType, $.fn.bsgrid.buildData.gridData(dataType, curPage, options.settings.localData), options)
                } else {
                    if (dataType == "xml") {
                        $.fn.bsgrid.loadGridData(dataType, "<xml>" + $.fn.bsgrid.buildData.gridData(dataType, curPage, options.settings.localData) + "</xml>", options)
                    }
                }
                return
            }
            $.ajax({
                type: options.settings.ajaxType,
                url: options.settings.url,
                data: $.fn.bsgrid.getPageCondition(curPage, options),
                dataType: dataType,
                beforeSend: function(XMLHttpRequest) {
                    if (options.settings.isProcessLockScreen) {
                        $.fn.bsgrid.lockScreen(options)
                    }
                    options.settings.beforeSend(options, XMLHttpRequest)
                },
                complete: function(XMLHttpRequest, textStatus) {
                    options.settings.complete(options, XMLHttpRequest, textStatus);
                    if (options.settings.isProcessLockScreen) {
                        $.fn.bsgrid.unlockScreen(options)
                    }
                },
                success: function(gridData, textStatus) {
                    $.fn.bsgrid.loadGridData(dataType, gridData, options)
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                    $.fn.bsgrid.alert($.bsgridLanguage.errorForSendOrRequestData)
                }
            })
        },
        loadGridData: function(dataType, gridData, options) {
            var parseSuccess = $.fn.bsgrid.parseData.success(dataType, gridData);
            for (var key in options.settings.extend.beforeRenderGridMethods) {
                options.settings.extend.beforeRenderGridMethods[key](parseSuccess, gridData, options)
            }
            options.settings.additionalBeforeRenderGrid(parseSuccess, gridData, options);
            if (parseSuccess) {
                var userdata = $.fn.bsgrid.parseData.userdata(dataType, gridData);
                $.fn.bsgrid.storeUserdata(userdata, options);
                options.settings.processUserdata(userdata, options);
                var totalRows = parseInt($.fn.bsgrid.parseData.totalRows(dataType, gridData));
                var curPage = parseInt($.fn.bsgrid.parseData.curPage(dataType, gridData));
                curPage = Math.max(curPage, 1);
                if (options.settings.pageAll) {
                    curPage = 1;
                    options.settings.pageSize = totalRows;
                    $("#" + options.noPagingationId).html(totalRows)
                }
                var pageSize = options.settings.pageSize;
                var totalPages = parseInt(totalRows / pageSize);
                totalPages = parseInt((totalRows % pageSize == 0) ? totalPages: totalPages + 1);
                var curPageRowsNum = $.fn.bsgrid.parseData.getDataLen(dataType, gridData);
                curPageRowsNum = curPageRowsNum > pageSize ? pageSize: curPageRowsNum;
                curPageRowsNum = (curPage * pageSize < totalRows) ? curPageRowsNum: (totalRows - (curPage - 1) * pageSize);
                var startRow = (curPage - 1) * pageSize + 1;
                var endRow = startRow + curPageRowsNum - 1;
                startRow = curPageRowsNum <= 0 ? 0 : startRow;
                endRow = curPageRowsNum <= 0 ? 0 : endRow;
                options.totalRows = totalRows;
                options.totalPages = totalPages;
                options.curPage = curPage;
                options.curPageRowsNum = curPageRowsNum;
                options.startRow = startRow;
                options.endRow = endRow;
                if (!options.settings.pageAll) {
                    $.fn.bsgrid.setPagingValues(options)
                }
                if (!options.settings.showPageToolbar || (options.settings.displayPagingToolbarOnlyMultiPages && totalPages <= 1)) {
                    $("#" + options.pagingId).hide();
                    $("#" + options.pagingOutTabId).hide()
                } else {
                    $("#" + options.pagingOutTabId).show();
                    $("#" + options.pagingId).show()
                }
                $.fn.bsgrid.setGridBlankBody(options);
                if (curPageRowsNum != 0) {
                    var data = $.fn.bsgrid.parseData.data(dataType, gridData);
                    var dataLen = data.length;
                    $.fn.bsgrid.addRowsClickEvent(options);
                    $.fn.bsgrid.getRows(options).each(function(i) {
                        var trObj = $(this);
                        var record = null;
                        if (i < curPageRowsNum && i < dataLen) {
                            record = $.fn.bsgrid.parseData.getRecord(dataType, data, dataLen != totalRows ? i: startRow + i - 1)
                        }
                        $.fn.bsgrid.storeRowData(i, record, options);
                        for (var key in options.settings.extend.renderPerRowMethods) {
                            options.settings.extend.renderPerRowMethods[key](record, i, trObj, options)
                        }
                        options.settings.additionalRenderPerRow(record, i, trObj, options);
                        for (var key in options.settings.event.customRowEvents) {
                            trObj.bind(key, {
                                record: record,
                                rowIndex: i,
                                trObj: trObj,
                                options: options
                            },
                            function(event) {
                                options.settings.event.customRowEvents[key](event.data.record, event.data.rowIndex, event.data.trObj, event.data.options)
                            })
                        }
                        var columnsModel = options.columnsModel;
                        $(this).find("td").each(function(j) {
                            var tdObj = $(this);
                            if (i < curPageRowsNum && i < dataLen) {
                                var index = columnsModel[j].index;
                                var render = columnsModel[j].render;
                                if (render != "") {
                                    var render_method = eval(render);
                                    var render_html = render_method(record, i, j, options);
                                    tdObj.html(render_html)
                                } else {
                                    if (index != "") {
                                        var value = $.fn.bsgrid.parseData.getColumnValue(dataType, record, index);
                                        if (columnsModel[j].tip == "true") {
                                            $.fn.bsgrid.columnTip(this, value, record)
                                        }
                                        if (options.settings.longLengthAotoSubAndTip) {
                                            $.fn.bsgrid.longLengthSubAndTip(this, value, columnsModel[j].maxLen, record)
                                        } else {
                                            tdObj.html(value)
                                        }
                                    }
                                }
                            } else {
                                tdObj.html("&nbsp;")
                            }
                            for (var key in options.settings.extend.renderPerColumnMethods) {
                                var renderPerColumn_html = options.settings.extend.renderPerColumnMethods[key](record, i, j, tdObj, trObj, options);
                                if (renderPerColumn_html != null && renderPerColumn_html != false) {
                                    tdObj.html(renderPerColumn_html)
                                }
                            }
                            options.settings.additionalRenderPerColumn(record, i, j, tdObj, trObj, options);
                            for (var key in options.settings.event.customCellEvents) {
                                tdObj.bind(key, {
                                    record: record,
                                    rowIndex: i,
                                    colIndex: j,
                                    tdObj: tdObj,
                                    trObj: trObj,
                                    options: options
                                },
                                function(event) {
                                    options.settings.event.customCellEvents[key](event.data.record, event.data.rowIndex, event.data.colIndex, event.data.tdObj, event.data.trObj, event.data.options)
                                })
                            }
                        })
                    })
                }
            } else {
                $.fn.bsgrid.alert($.bsgridLanguage.errorForRequestData)
            }
            for (var key in options.settings.extend.afterRenderGridMethods) {
                options.settings.extend.afterRenderGridMethods[key](parseSuccess, gridData, options)
            }
            options.settings.additionalAfterRenderGrid(parseSuccess, gridData, options)
        },
        reloadLocalData: function(localData, options) {
            options.settings.localData = localData;
            $.fn.bsgrid.page(1, options)
        },
        addRowsClickEvent: function(options) {
            $.fn.bsgrid.getRows(options).filter(":lt(" + options.curPageRowsNum + ")").click(function() {
                if ($(this).hasClass("selected")) {
                    $.fn.bsgrid.unSelectRow(options)
                } else {
                    $.fn.bsgrid.selectRow($.fn.bsgrid.getRows(options).index($(this)), options)
                }
            })
        },
        getRows: function(options) {
            return $("#" + options.gridId + " tbody tr")
        },
        getRow: function(row, options) {
            return $.fn.bsgrid.getRows(options).eq(row)
        },
        getRowCells: function(row, options) {
            return $.fn.bsgrid.getRow(row, options).find("td")
        },
        getColCells: function(col, options) {
            return $.fn.bsgrid.getRows(options).find("td:nth-child(" + (col + 1) + ")")
        },
        getCell: function(row, col, options) {
            return $.fn.bsgrid.getRowCells(row, options).eq(col)
        },
        getSelectedRow: function(options) {
            return $.fn.bsgrid.getRows(options).filter(".selected")
        },
        getSelectedRowIndex: function(options) {
            return $.fn.bsgrid.getRows(options).index($.fn.bsgrid.getSelectedRow(options))
        },
        selectRow: function(row, options) {
            $.fn.bsgrid.unSelectRow(options);
            var trObj = $.fn.bsgrid.getRow(row, options);
            trObj.addClass("selected");
            if (options.settings.rowSelectedColor) {
                trObj.addClass("selected_color")
            }
            if ( !! options.settings.event.selectRowEvent) {
                options.settings.event.selectRowEvent($.fn.bsgrid.getRowRecord(trObj), row, trObj, options)
            }
        },
        unSelectRow: function(options) {
            var row = $.fn.bsgrid.getSelectedRowIndex(options);
            if (row != -1) {
                var trObj = $.fn.bsgrid.getRow(row, options);
                trObj.removeClass("selected").removeClass("selected_color");
                if ( !! options.settings.event.unselectRowEvent) {
                    options.settings.event.unselectRowEvent($.fn.bsgrid.getRowRecord(trObj), row, trObj, options)
                }
            }
        },
        getUserdata: function(options) {
            $("#" + options.gridId).data("userdata")
        },
        storeUserdata: function(userdata, options) {
            $("#" + options.gridId).data("userdata", userdata)
        },
        getRowRecord: function(rowObj) {
            var record = rowObj.data("record");
            return record == undefined ? null: record
        },
        storeRowData: function(row, record, options) {
            $.fn.bsgrid.getRow(row, options).data("record", record)
        },
        getAllRecords: function(options) {
            var records = [];
            $.fn.bsgrid.getRows(options).each(function() {
                var record = $.fn.bsgrid.getRowRecord($(this));
                if (record != null) {
                    records[records.length] = record
                }
            });
            return records
        },
        getRecord: function(row, options) {
            return $.fn.bsgrid.getRowRecord($.fn.bsgrid.getRow(row, options))
        },
        getRecordIndexValue: function(record, index, options) {
            if (record == null) {
                return ""
            } else {
                return $.fn.bsgrid.parseData.getColumnValue(options.settings.dataType, record, index)
            }
        },
        getColumnValue: function(row, index, options) {
            var record = $.fn.bsgrid.getRecord(row, options);
            return $.fn.bsgrid.getRecordIndexValue(record, index, options)
        },
        getCellRecordValue: function(row, col, options) {
            var index = $.trim($.fn.bsgrid.getColumnModel(col, options).index);
            if (index == "") {
                return ""
            } else {
                return $.fn.bsgrid.getColumnValue(row, index, options)
            }
        },
        sort: function(obj, options) {
            options.sortName = "";
            options.sortOrder = "";
            var aObj = $(obj).find("a");
            var field = $(aObj).attr("sortName");
            var columnsModel = options.columnsModel;
            $.fn.bsgrid.getGridHeaderObject(options).each(function(i) {
                var sortName = columnsModel[i].sortName;
                if (sortName != "") {
                    var sortOrder = $.fn.bsgrid.getSortOrder($(this), options);
                    if (!options.settings.multiSort && sortName != field) {
                        $(this).find("a").attr("class", "sort sort-view")
                    } else {
                        if (sortName == field) {
                            if (sortOrder == "") {
                                sortOrder = "desc"
                            } else {
                                if (sortOrder == "desc") {
                                    sortOrder = "asc"
                                } else {
                                    if (sortOrder == "asc") {
                                        sortOrder = ""
                                    }
                                }
                            }
                            $(this).find("a").attr("class", "sort sort-" + (sortOrder == "" ? "view": sortOrder))
                        }
                        if (sortOrder != "") {
                            options.sortName = ($.trim(options.sortName) == "") ? sortName: (options.sortName + "," + sortName);
                            options.sortOrder = ($.trim(options.sortOrder) == "") ? sortOrder: (options.sortOrder + "," + sortOrder)
                        }
                    }
                }
            });
            $.fn.bsgrid.refreshPage(options)
        },
        getSortOrder: function(obj, options) {
            var sortOrder = $.trim($(obj).find("a").attr("class"));
            if (sortOrder == "sort sort-view") {
                sortOrder = ""
            } else {
                if (sortOrder == "sort sort-asc") {
                    sortOrder = "asc"
                } else {
                    if (sortOrder == "sort sort-desc") {
                        sortOrder = "desc"
                    } else {
                        sortOrder = ""
                    }
                }
            }
            return sortOrder
        },
        getGridHeaderObject: function(options) {
            return $("#" + options.gridId + " thead tr:last").find("th")
        },
        getColumnModel: function(colIndex, options) {
            return options.columnsModel[colIndex]
        },
        appendHeaderSort: function(options) {
            var columnsModel = options.columnsModel;
            $.fn.bsgrid.getGridHeaderObject(options).each(function(i) {
                if (columnsModel[i].sortName != "") {
                    var sortName = columnsModel[i].sortName;
                    var sortOrder = columnsModel[i].sortOrder;
                    var sortHtml = '<a href="javascript:void(0);" sortName="' + sortName + '" class="sort ';
                    if (sortOrder != "" && (sortOrder == "desc" || sortOrder == "asc")) {
                        options.sortName = ($.trim(options.sortName) == "") ? sortName: (options.sortName + "," + sortName);
                        options.sortOrder = ($.trim(options.sortOrder) == "") ? sortOrder: (options.sortOrder + "," + sortOrder);
                        sortHtml += "sort-" + sortOrder
                    } else {
                        sortHtml += "sort-view"
                    }
                    sortHtml += '">&nbsp;&nbsp;&nbsp;</a>';
                    $(this).append(sortHtml).find(".sort").click(function() {
                        $.fn.bsgrid.sort($(this).parent("th"), options)
                    })
                }
            })
        },
        setGridBlankBody: function(options) {
            $.fn.bsgrid.getRows(options).remove();
            var header = $.fn.bsgrid.getGridHeaderObject(options);
            var columnsModel = options.columnsModel;
            for (var hi = 0; hi < header.length; hi++) {
                if (columnsModel[hi].hidden == "true") {
                    header.eq(hi).css("display", "none")
                }
            }
            var rowSb = "";
            if (options.settings.pageSize > 0) {
                var trSb = new StringBuilder();
                trSb.append("<tr>");
                for (var hi = 0; hi < header.length; hi++) {
                    trSb.append('<td style="text-align: ' + columnsModel[hi].align + ";");
                    if (columnsModel[hi].hidden == "true") {
                        trSb.append(" display: none;")
                    }
                    trSb.append('"');
                    trSb.append(">&nbsp;</td>")
                }
                trSb.append("</tr>");
                rowSb = trSb.toString()
            }
            var rowsSb = new StringBuilder();
            var curPageRowsNum = options.settings.pageSize;
            if (!options.settings.displayBlankRows) {
                curPageRowsNum = options.endRow - options.startRow + 1;
                curPageRowsNum = options.endRow > 0 ? curPageRowsNum: 0
            }
            if (curPageRowsNum == 0) {
                rowsSb.append('<tr><td colspan="' + header.length + '">' + $.bsgridLanguage.noDataToDisplay + "</td></tr>")
            } else {
                for (var pi = 0; pi < curPageRowsNum; pi++) {
                    rowsSb.append(rowSb)
                }
            }
            $("#" + options.gridId + " tbody").append(rowsSb.toString());
            if (curPageRowsNum != 0) {
                if (options.settings.stripeRows) {
                    $.fn.bsgrid.getRows(options).filter(":even").addClass("even_index_row")
                }
                if (options.settings.rowHoverColor) {
                    $("#" + options.gridId + " tbody tr").hover(function() {
                        $(this).addClass("row_hover")
                    },
                    function() {
                        $(this).removeClass("row_hover")
                    })
                }
            }
            if (!options.settings.lineWrap) {
                $.fn.bsgrid.getRows(options).find("td").addClass("lineNoWrap")
            } else {
                $.fn.bsgrid.getRows(options).find("td").addClass("lineWrap")
            }
        },
        createPagingOutTab: function(options) {
            var pagingOutTabSb = new StringBuilder();
            pagingOutTabSb.append('<table id="' + options.pagingOutTabId + '" class="bsgridPagingOutTab" style="display: none;"><tr><td align="' + options.settings.pagingToolbarAlign + '">');
            if (options.settings.pageAll) {
                pagingOutTabSb.append($.bsgridLanguage.noPagingation(options.noPagingationId) + "&nbsp;&nbsp;&nbsp;")
            }
            pagingOutTabSb.append("</td></tr></table>");
            $("#" + options.gridId).after(pagingOutTabSb.toString())
        },
        clearGridBodyData: function(options) {
            $.fn.bsgrid.getRows(options).find("td").html("&nbsp;")
        },
        addLockScreen: function(options) {
            if ($(".bsgrid.lockscreen").length == 0) {
                var lockScreenHtml = new StringBuilder();
                lockScreenHtml.append('<div class="bsgrid lockscreen" times="0">');
                lockScreenHtml.append("</div>");
                lockScreenHtml.append('<div class="bsgrid loading_div">');
                lockScreenHtml.append('<table><tr><td><center><div class="bsgrid loading"><span>&nbsp;&emsp;</span>&nbsp;' + $.bsgridLanguage.loadingDataMessage + "&emsp;<center></div></td></tr></table>");
                lockScreenHtml.append("</div>");
                $("body").append(lockScreenHtml.toString())
            }
        },
        lockScreen: function(options) {
            $(".bsgrid.lockscreen").attr("times", parseInt($(".bsgrid.lockscreen").attr("times")) + 1);
            if ($(".bsgrid.lockscreen").css("display") == "none") {
                $(".bsgrid.lockscreen").show();
                $(".bsgrid.loading_div").show()
            }
        },
        unlockScreen: function(options) {
            $(".bsgrid.lockscreen").attr("times", parseInt($(".bsgrid.lockscreen").attr("times")) - 1);
            if ($(".bsgrid.lockscreen").attr("times") == "0") {
                setTimeout(function() {
                    $(".bsgrid.lockscreen").hide();
                    $(".bsgrid.loading_div").hide()
                },
                50)
            }
        },
        columnTip: function(obj, value, record) {
            $(obj).attr("title", value)
        },
        alert: function(msg) {
            try {
                $.bsgrid.alert(msg)
            } catch(e) {
                alert(msg)
            }
        },
        longLengthSubAndTip: function(obj, value, maxLen, record) {
            var tip = false;
            if (value.length > maxLen) {
                try {
                    if (value.indexOf("<") < 0 || value.indexOf(">") < 2 || $(value).text().length == 0) {
                        tip = true
                    }
                } catch(e) {
                    tip = true
                }
            }
            if (tip) {
                $(obj).html(value.substring(0, maxLen - 3) + "...");
                $.fn.bsgrid.columnTip(obj, value, record)
            } else {
                $(obj).html(value)
            }
        },
        getPagingObj: function(options) {
            return $.fn.bsgrid.getGridObj(options.gridId).pagingObj
        },
        getCurPage: function(options) {
            return $.fn.bsgrid.getPagingObj(options).getCurPage()
        },
        refreshPage: function(options) {
            if (!options.settings.pageAll) {
                $.fn.bsgrid.getPagingObj(options).refreshPage()
            } else {
                $.fn.bsgrid.page(1, options)
            }
        },
        firstPage: function(options) {
            $.fn.bsgrid.getPagingObj(options).firstPage()
        },
        prevPage: function(options) {
            $.fn.bsgrid.getPagingObj(options).prevPage()
        },
        nextPage: function(options) {
            $.fn.bsgrid.getPagingObj(options).nextPage()
        },
        lastPage: function(options) {
            $.fn.bsgrid.getPagingObj(options).lastPage()
        },
        gotoPage: function(options, goPage) {
            $.fn.bsgrid.getPagingObj(options).gotoPage(goPage)
        },
        initPaging: function(options) {
            $("#" + options.pagingOutTabId + " td").attr("id", options.pagingId);
            return $.fn.bsgrid_paging.init(options.pagingId, {
                gridId: options.gridId,
                pageSize: options.settings.pageSize,
                pageSizeSelect: options.settings.pageSizeSelect,
                pageSizeForGrid: options.settings.pageSizeForGrid,
                pageIncorrectTurnAlert: options.settings.pageIncorrectTurnAlert,
                pagingLittleToolbar: options.settings.pagingLittleToolbar,
                pagingBtnClass: options.settings.pagingBtnClass
            })
        },
        setPagingValues: function(options) {
            $.fn.bsgrid.getPagingObj(options).setPagingValues(options.curPage, options.totalRows)
        }
    }
})(jQuery); (function($) {
    $.fn.bsgrid.defaults.extend.settings = {
        supportGridEdit: false,
        supportGridEditTriggerEvent: "rowClick",
        supportColumnMove: false,
        searchConditionsContainerId: "",
        fixedGridHeader: false,
        fixedGridHeight: "320px",
        gridEditConfigs: {
            text: {
                build: function(edit, value, record, rowIndex, colIndex, tdObj, trObj, options) {
                    return value + '<input class="bsgrid_editgrid_edit" type="' + edit + '" value="' + value + '"/>'
                },
                val: function(formObj) {
                    return formObj.val()
                }
            },
            checkbox: {
                build: function(edit, value, record, rowIndex, colIndex, tdObj, trObj, options) {
                    return value + '<input class="bsgrid_editgrid_checkbox" type="' + edit + '" value="' + value + '"/>'
                },
                val: function(formObj) {
                    return formObj.val()
                }
            },
            textarea: {
                build: function(edit, value, record, rowIndex, colIndex, tdObj, trObj, options) {
                    return value + '<textarea class="bsgrid_editgrid_edit">' + value + "</textarea>"
                },
                val: function(formObj) {
                    return formObj.val()
                }
            }
        }
    };
    $.fn.bsgrid.defaults.extend.settings.gridEditConfigs.hidden = $.fn.bsgrid.defaults.extend.settings.gridEditConfigs.text;
    $.fn.bsgrid.defaults.extend.settings.gridEditConfigs.password = $.fn.bsgrid.defaults.extend.settings.gridEditConfigs.text;
    $.fn.bsgrid.defaults.extend.settings.gridEditConfigs.radio = $.fn.bsgrid.defaults.extend.settings.gridEditConfigs.text;
    $.fn.bsgrid.defaults.extend.settings.gridEditConfigs.button = $.fn.bsgrid.defaults.extend.settings.gridEditConfigs.text;
    $.extend(true, $.fn.bsgrid.defaults.colsProperties, {
        lineNumberAttr: "w_num",
        checkAttr: "w_check",
        editAttr: "w_edit",
        aggAttr: "w_agg"
    });
    $.fn.bsgrid.defaults.event.customCellEditEvents = {};
    $.fn.bsgrid.extendInitGrid = {};
    $.fn.bsgrid.extendBeforeRenderGrid = {};
    $.fn.bsgrid.extendRenderPerRow = {};
    $.fn.bsgrid.extendRenderPerColumn = {};
    $.fn.bsgrid.extendAfterRenderGrid = {};
    $.fn.bsgrid.extendOtherMethods = {};
    $.fn.bsgrid.extendInitGrid.initGridExtendOptions = function(gridId, options) {
        var columnsModel = options.columnsModel;
        var colsProperties = options.settings.colsProperties;
        $.fn.bsgrid.getGridHeaderObject(options).each(function(i) {
            columnsModel[i].lineNumber = $.trim($(this).attr(colsProperties.lineNumberAttr));
            columnsModel[i].check = $.trim($(this).attr(colsProperties.checkAttr));
            columnsModel[i].edit = $.trim($(this).attr(colsProperties.editAttr))
        });
        if ($("#" + options.gridId + " tfoot tr td[" + colsProperties.aggAttr + "!='']").length != 0) {
            $("#" + options.gridId + " tfoot tr td").each(function(i) {
                columnsModel[i].aggName = "";
                columnsModel[i].aggIndex = "";
                var aggInfo = $.trim($(this).attr(colsProperties.aggAttr));
                if (aggInfo.length != 0) {
                    var aggInfoArray = aggInfo.split(",");
                    columnsModel[i].aggName = aggInfoArray[0].toLocaleLowerCase();
                    columnsModel[i].aggIndex = aggInfoArray.length > 1 ? aggInfoArray[1] : ""
                }
            })
        }
        if ($.fn.bsgrid.getGridHeaderObject(options).filter("[" + colsProperties.lineNumberAttr + "$='line']").length != 0) {
            options.settings.extend.afterRenderGridMethods.renderLineNumber = $.fn.bsgrid.extendAfterRenderGrid.renderLineNumber
        }
        if ($.fn.bsgrid.getGridHeaderObject(options).filter("[" + colsProperties.checkAttr + "='true']").length != 0) {
            options.settings.extend.initGridMethods.initGridCheck = $.fn.bsgrid.extendInitGrid.initGridCheck;
            options.settings.extend.renderPerColumnMethods.renderCheck = $.fn.bsgrid.extendRenderPerColumn.renderCheck;
            options.settings.extend.afterRenderGridMethods.addCheckChangedEvent = $.fn.bsgrid.extendAfterRenderGrid.addCheckChangedEvent
        }
        if (options.settings.extend.settings.supportGridEdit) {
            options.settings.extend.renderPerColumnMethods.renderForm = $.fn.bsgrid.extendRenderPerColumn.renderForm;
            options.settings.extend.afterRenderGridMethods.addGridEditEvent = $.fn.bsgrid.extendAfterRenderGrid.addGridEditEvent;
            var gridObj = $.fn.bsgrid.getGridObj(gridId);
            gridObj.activeGridEditMode = function() {
                return $.fn.bsgrid.defaults.extend.activeGridEditMode(options)
            };
            gridObj.getChangedRowsIndexs = function() {
                return $.fn.bsgrid.defaults.extend.getChangedRowsIndexs(options)
            };
            gridObj.getChangedRowsOldRecords = function() {
                return $.fn.bsgrid.defaults.extend.getChangedRowsOldRecords(options)
            };
            gridObj.getRowsChangedColumnsValue = function() {
                return $.fn.bsgrid.defaults.extend.getRowsChangedColumnsValue(options)
            };
            gridObj.deleteRow = function(row) {
                $.fn.bsgrid.defaults.extend.deleteRow(row, options)
            };
            gridObj.addNewEditRow = function() {
                $.fn.bsgrid.defaults.extend.addNewEditRow(options)
            }
        }
        if (options.settings.extend.settings.supportColumnMove) {
            options.settings.extend.initGridMethods.initColumnMove = $.fn.bsgrid.extendInitGrid.initColumnMove
        }
        if (options.settings.extend.settings.fixedGridHeader) {
            options.settings.extend.initGridMethods.initFixedHeader = $.fn.bsgrid.extendOtherMethods.initFixedHeader;
            options.settings.extend.afterRenderGridMethods.fixedHeader = function(parseSuccess, gridData, options) {
                $.fn.bsgrid.extendOtherMethods.fixedHeader(false, options)
            }
        }
        if ($.trim(options.settings.extend.settings.searchConditionsContainerId) != "") {
            options.settings.extend.initGridMethods.initSearchConditions = $.fn.bsgrid.extendInitGrid.initSearchConditions
        }
        if ($("#" + options.gridId + " tfoot td[" + colsProperties.aggAttr + "!='']").length != 0) {
            options.settings.extend.afterRenderGridMethods.aggregation = $.fn.bsgrid.extendAfterRenderGrid.aggregation
        }
    };
    $.fn.bsgrid.extendInitGrid.initGridCheck = function(gridId, options) {
        $.fn.bsgrid.getGridHeaderObject(options).each(function(hi) {
            if (options.columnsModel[hi].check == "true") {
                if ($.trim($(this).html()) == "") {
                    $(this).html('<input class="bsgrid_editgrid_check" type="checkbox"/>')
                }
                $(this).find("input[type=checkbox]").change(function() {
                    var checked = $.bsgrid.adaptAttrOrProp($(this), "checked") ? true: false;
                    $.bsgrid.adaptAttrOrProp($.fn.bsgrid.getRows(options).find("td:nth-child(" + (hi + 1) + ")>input[type=checkbox]"), "checked", checked)
                })
            }
        });
        var gridObj = $.fn.bsgrid.getGridObj(gridId);
        gridObj.getCheckedRowsIndexs = function() {
            return $.fn.bsgrid.defaults.extend.getCheckedRowsIndexs(options)
        };
        gridObj.getCheckedRowsRecords = function() {
            return $.fn.bsgrid.defaults.extend.getCheckedRowsRecords(options)
        };
        gridObj.getCheckedValues = function(index) {
            return $.fn.bsgrid.defaults.extend.getCheckedValues(index, options)
        }
    };
    $.fn.bsgrid.extendInitGrid.initSearchConditions = function(gridId, options) {
        var conditionsHtml = new StringBuilder();
        conditionsHtml.append('<select class="bsgrid_conditions_select">');
        var params = {};
        $.fn.bsgrid.getGridHeaderObject(options).each(function(i) {
            var index = options.columnsModel[i].index;
            var text = $.trim($(this).text());
            if (index != "" && text != "" && $.trim(params[index]) == "") {
                params[index] = text
            }
        });
        for (var key in params) {
            conditionsHtml.append('<option value="' + key + '">' + params[key] + "</option>")
        }
        conditionsHtml.append("</select>");
        conditionsHtml.append("&nbsp;");
        conditionsHtml.append('<input type="text" class="bsgrid_conditions_input" />');
        $("#" + options.settings.extend.settings.searchConditionsContainerId).html(conditionsHtml.toString());
        $("#" + options.settings.extend.settings.searchConditionsContainerId + " select.bsgrid_conditions_select").change(function() {
            $(this).next("input.bsgrid_conditions_input").attr("name", $(this).val())
        }).trigger("change")
    };
    $.fn.bsgrid.extendInitGrid.initColumnMove = function(gridId, options) {
        if ($("#" + options.gridId + " thead tr").length != 1) {
            return
        }
        $("#" + options.gridId).css({
            "table-layout": "fixed"
        });
        var headObj = $.fn.bsgrid.getGridHeaderObject(options);
        var headLen = headObj.length;
        headObj.each(function(i) {
            var obj = this;
            $(obj).bind("selectstart",
            function() {
                return false
            });
            $(obj).css("-moz-user-select", "none");
            $(obj).mousedown(function() {
                bindDownData(obj, i, headLen)
            });
            $(obj).mousemove(function(e) {
                e = e || event;
                var left = $(obj).offset().left;
                var nObj = 0,
                nLeft = 0;
                if (i != headLen - 1) {
                    nObj = $(obj).next();
                    nLeft = nObj.offset().left
                }
                var mObj = obj;
                if (i != headLen - 1 && e.clientX - nLeft > -10) {
                    mObj = nObj
                }
                if ((i != 0 && e.clientX - left < 10) || (i != headLen - 1 && e.clientX - nLeft > -10)) {
                    $(obj).css({
                        cursor: "e-resize"
                    });
                    if ($.trim($(obj).data("ex_mousedown")) != "mousedown") {
                        return
                    }
                    var mWidth = $(mObj).width();
                    var newMWidth = mWidth - e.clientX + $(mObj).offset().left;
                    var preMWidth = $(mObj).prev().width();
                    var preNewMWidth = preMWidth + e.clientX - $(mObj).offset().left;
                    if (parseInt(newMWidth) > 19 && parseInt(preNewMWidth) > 19) {
                        $(mObj).width(newMWidth).prev().width(preNewMWidth)
                    }
                } else {
                    $(mObj).css({
                        cursor: "default"
                    });
                    releaseDownData(obj, i, headLen)
                }
            });
            $(obj).mouseup(function() {
                releaseDownData(obj, i, headLen)
            });
            $(obj).mouseout(function(e) {
                e = e || event;
                var objOffect = $(obj).offset();
                if (objOffect.top > e.clientY || objOffect.top + $(obj).height() < e.clientY) {
                    releaseDownData(obj, i, headLen)
                }
            });
            function bindDownData(obj, i, headLen) {
                if (i != 0) {
                    $(obj).prev().data("ex_mousedown", "mousedown")
                }
                $(obj).data("ex_mousedown", "mousedown");
                if (i != headLen - 1) {
                    $(obj).next().data("ex_mousedown", "mousedown")
                }
            }
            function releaseDownData(obj, i, headLen) {
                if (i != 0) {
                    $(obj).prev().data("ex_mousedown", "")
                }
                $(obj).data("ex_mousedown", "");
                if (i != headLen - 1) {
                    $(obj).next().data("ex_mousedown", "")
                }
            }
        })
    };
    $.fn.bsgrid.extendRenderPerColumn.renderCheck = function(record, rowIndex, colIndex, tdObj, trObj, options) {
        if (rowIndex < options.curPageRowsNum) {
            var columnModel = options.columnsModel[colIndex];
            if (columnModel.check == "true") {
                return '<input class="bsgrid_editgrid_check" type="checkbox" value="' + $.fn.bsgrid.getRecordIndexValue(record, columnModel.index, options) + '"/>'
            }
        }
        return false
    };
    $.fn.bsgrid.extendRenderPerColumn.renderForm = function(record, rowIndex, colIndex, tdObj, trObj, options) {
        if (rowIndex < options.curPageRowsNum) {
            var columnModel = options.columnsModel[colIndex];
            var edit = columnModel.edit;
            var value = $.fn.bsgrid.getRecordIndexValue(record, columnModel.index, options);
            var tdHtml = "&nbsp;";
            if (edit in options.settings.extend.settings.gridEditConfigs) {
                tdHtml = options.settings.extend.settings.gridEditConfigs[edit].build(edit, value, record, rowIndex, colIndex, tdObj, trObj, options)
            } else {
                return false
            }
            tdObj.html(tdHtml);
            tdObj.find(":input").addClass("bsgrid_editgrid_hidden");
            for (var key in options.settings.event.customCellEditEvents) {
                tdObj.find(":input").each(function() {
                    var formObj = $(this);
                    formObj.bind(key, {
                        formObj: formObj,
                        record: record,
                        rowIndex: rowIndex,
                        colIndex: colIndex,
                        tdObj: tdObj,
                        trObj: trObj,
                        options: options
                    },
                    function(event) {
                        options.settings.event.customCellEditEvents[key](event.data.formObj, event.data.record, event.data.rowIndex, event.data.colIndex, event.data.tdObj, event.data.trObj, event.data.options)
                    })
                })
            }
        }
        return false
    };
    $.fn.bsgrid.extendAfterRenderGrid.renderLineNumber = function(parseSuccess, gridData, options) {
        $.fn.bsgrid.getGridHeaderObject(options).each(function(i) {
            var num = options.columnsModel[i].lineNumber;
            if (num == "line" || num == "total_line") {
                $.fn.bsgrid.getRows(options).filter(":lt(" + options.curPageRowsNum + ")").find("td:nth-child(" + (i + 1) + ")").each(function(li) {
                    $(this).html((num == "line") ? (li + 1) : (li + options.startRow))
                })
            }
        })
    };
    $.fn.bsgrid.extendAfterRenderGrid.addCheckChangedEvent = function(parseSuccess, gridData, options) {
        $.fn.bsgrid.getGridHeaderObject(options).each(function(hi) {
            if (options.columnsModel[hi].check == "true") {
                var checkboxObj = $(this).find("input[type=checkbox]");
                var checkboxObjs = $.fn.bsgrid.getRows(options).find("td:nth-child(" + (hi + 1) + ")>input[type=checkbox]");
                checkboxObjs.change(function() {
                    var allCheckboxObjs = $.fn.bsgrid.getRows(options).find("td:nth-child(" + (hi + 1) + ")>input[type=checkbox]");
                    var checked = $.bsgrid.adaptAttrOrProp(checkboxObj, "checked") ? true: false;
                    if (!checked && allCheckboxObjs.filter(":checked").length == allCheckboxObjs.length) {
                        $.bsgrid.adaptAttrOrProp(checkboxObj, "checked", true)
                    } else {
                        if (checked && allCheckboxObjs.filter(":checked").length != allCheckboxObjs.length) {
                            $.bsgrid.adaptAttrOrProp(checkboxObj, "checked", false)
                        }
                    }
                })
            }
        })
    };
    $.fn.bsgrid.extendAfterRenderGrid.addGridEditEvent = function(parseSuccess, gridData, options) {
        var gridObj = $.fn.bsgrid.getGridObj(options.gridId);
        $.fn.bsgrid.getRows(options).filter(":lt(" + options.curPageRowsNum + ")").each(function() {
            var columnsModel = options.columnsModel;
            $(this).find("td").each(function(ci) {
                if (columnsModel[ci].edit != "") {
                    $(this).find(":input").change(function() {
                        var rowObj = $(this).parent("td").parent("tr");
                        var isNew = $.trim(rowObj.data("new"));
                        var value = (isNew == "true" ? "": gridObj.getRecordIndexValue(gridObj.getRowRecord(rowObj), columnsModel[ci].index));
                        if ($.trim($(this).val()) != value) {
                            $(this).addClass("bsgrid_editgrid_change")
                        } else {
                            $(this).removeClass("bsgrid_editgrid_change")
                        }
                        rowObj.data("change", rowObj.find(".bsgrid_editgrid_change").length)
                    })
                }
            });
            if (options.settings.extend.settings.supportGridEditTriggerEvent == "") {
                $(this).find(".bsgrid_editgrid_hidden").each(function() {
                    showCellEdit(this)
                })
            } else {
                if (options.settings.extend.settings.supportGridEditTriggerEvent == "rowClick") {
                    $(this).click(function() {
                        $(this).find(".bsgrid_editgrid_hidden").each(function() {
                            showCellEdit(this)
                        })
                    })
                } else {
                    if (options.settings.extend.settings.supportGridEditTriggerEvent == "rowDoubleClick") {
                        $(this).dblclick(function() {
                            $(this).find(".bsgrid_editgrid_hidden").each(function() {
                                showCellEdit(this)
                            })
                        })
                    } else {
                        if (options.settings.extend.settings.supportGridEditTriggerEvent == "cellClick") {
                            $(this).find(".bsgrid_editgrid_hidden").each(function() {
                                var formObj = this;
                                $(formObj).parent("td").click(function() {
                                    showCellEdit(formObj)
                                })
                            })
                        } else {
                            if (options.settings.extend.settings.supportGridEditTriggerEvent == "cellDoubleClick") {
                                $(this).find(".bsgrid_editgrid_hidden").each(function() {
                                    var formObj = this;
                                    $(formObj).parent("td").dblclick(function() {
                                        showCellEdit(formObj)
                                    })
                                })
                            }
                        }
                    }
                }
            }
        });
        function showCellEdit(formObj) {
            var cloneObj = $(formObj).removeClass("bsgrid_editgrid_hidden").clone(true);
            $(formObj).parent("td").html(cloneObj)
        }
    };
    $.fn.bsgrid.extendAfterRenderGrid.aggregation = function(parseSuccess, gridData, options) {
        var gridObj = $.fn.bsgrid.getGridObj(options.gridId);
        var columnsModel = options.columnsModel;
        $("#" + options.gridId + " tfoot tr td[" + options.settings.colsProperties.aggAttr + "!='']").each(function(i) {
            if (columnsModel[i].aggName != "") {
                var aggName = columnsModel[i].aggName;
                var val = null;
                if (aggName == "count") {
                    val = options.curPageRowsNum
                } else {
                    if (aggName == "countnotnone" || aggName == "sum" || aggName == "avg" || aggName == "max" || aggName == "min" || aggName == "concat") {
                        if (aggName == "countnotnone") {
                            val = 0
                        }
                        var valHtml = new StringBuilder();
                        $.fn.bsgrid.getRows(options).filter(":lt(" + options.curPageRowsNum + ")").each(function(ri) {
                            var rval = gridObj.getColumnValue(ri, columnsModel[i].aggIndex);
                            if (rval == "") {} else {
                                if (aggName == "countnotnone") {
                                    val = (val == null ? 0 : val) + 1
                                } else {
                                    if (aggName == "sum" || aggName == "avg") {
                                        if (!isNaN(rval)) {
                                            val = (val == null ? 0 : val) + parseFloat(rval)
                                        }
                                    } else {
                                        if (aggName == "max" || aggName == "min") {
                                            if (!isNaN(rval) && (val == null || (aggName == "max" && parseFloat(rval) > val) || (aggName == "min" && parseFloat(rval) < val))) {
                                                val = parseFloat(rval)
                                            }
                                        } else {
                                            if (aggName == "concat") {
                                                valHtml.append(rval)
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        if (aggName == "avg" && val != null) {
                            val = val / options.curPageRowsNum
                        } else {
                            if (aggName == "concat") {
                                val = valHtml.toString()
                            }
                        }
                    } else {
                        if (aggName == "custom") {
                            val = eval(columnsModel[i].aggIndex)(gridObj, options)
                        }
                    }
                }
                val = val == null ? "": val;
                $(this).html(val)
            }
        })
    };
    $.fn.bsgrid.defaults.extend.getCheckedRowsIndexs = function(options) {
        var rowIndexs = [];
        $.fn.bsgrid.getRows(options).each(function(i) {
            if ($(this).find("td>input:checked").length == 1) {
                rowIndexs[rowIndexs.length] = i
            }
        });
        return rowIndexs
    };
    $.fn.bsgrid.defaults.extend.getCheckedRowsRecords = function(options) {
        var records = [];
        $.each($.fn.bsgrid.defaults.extend.getCheckedRowsIndexs(options),
        function(i, rowIndex) {
            records[records.length] = $.fn.bsgrid.getRecord(rowIndex, options)
        });
        return records
    };
    $.fn.bsgrid.defaults.extend.getCheckedValues = function(index, options) {
        var values = [];
        $.each($.fn.bsgrid.defaults.extend.getCheckedRowsRecords(options),
        function(i, record) {
            values[values.length] = $.fn.bsgrid.getRecordIndexValue(record, index, options)
        });
        return values
    };
    $.fn.bsgrid.defaults.extend.activeGridEditMode = function(options) {
        if (!options.settings.extend.settings.supportGridEdit) {
            return
        }
        $.fn.bsgrid.getRows(options).filter(":lt(" + options.curPageRowsNum + ")").find("td .bsgrid_editgrid_hidden").each(function() {
            var cloneObj = $(this).removeClass("bsgrid_editgrid_hidden").clone(true);
            $(this).parent("td").html(cloneObj)
        })
    };
    $.fn.bsgrid.defaults.extend.getChangedRowsIndexs = function(options) {
        var rowIndexs = [];
        $.fn.bsgrid.getRows(options).each(function(i) {
            var cellChangedNumStr = $.trim($(this).data("change"));
            if (!isNaN(cellChangedNumStr) && parseInt(cellChangedNumStr) > 0) {
                rowIndexs[rowIndexs.length] = i
            }
        });
        return rowIndexs
    };
    $.fn.bsgrid.defaults.extend.getChangedRowsOldRecords = function(options) {
        var records = [];
        $.each($.fn.bsgrid.defaults.extend.getChangedRowsIndexs(options),
        function(i, rowIndex) {
            records[records.length] = $.fn.bsgrid.getRecord(rowIndex, options)
        });
        return records
    };
    $.fn.bsgrid.defaults.extend.getRowsChangedColumnsValue = function(options) {
        var values = {};
        $.each($.fn.bsgrid.defaults.extend.getChangedRowsIndexs(options),
        function(i, rowIndex) {
            values["row_" + rowIndex] = {};
            $.fn.bsgrid.getRows(options).filter(":eq(" + rowIndex + ")").find("td").each(function(ci) {
                if ($(this).find(".bsgrid_editgrid_change").length > 0) {
                    values["row_" + rowIndex][options.columnsModel[ci].index] = options.settings.extend.settings.gridEditConfigs[options.columnsModel[ci].edit].val($(this).find(".bsgrid_editgrid_change"))
                }
            })
        });
        return values
    };
    $.fn.bsgrid.defaults.extend.deleteRow = function(row, options) {
        $.fn.bsgrid.getRow(row, options).remove()
    };
    $.fn.bsgrid.defaults.extend.addNewEditRow = function(options) {
        var gridObj = $.fn.bsgrid.getGridObj(options.gridId);
        if (gridObj.getRows().length < 1) {
            return
        }
        $("#" + options.gridId + " tbody").prepend(gridObj.getRow(0).clone(true));
        gridObj.getRowCells(0).each(function(colIndex) {
            var columnModel = options.columnsModel[colIndex];
            if (columnModel.render != "") {
                var render_method = eval(columnModel.render);
                var render_html = render_method(null, 0, colIndex, options);
                $(this).html(render_html)
            } else {
                if (columnModel.edit != "textarea") {
                    $(this).children().val("")
                } else {
                    $(this).children().text("")
                }
                $(this).html($(this).children().removeClass("bsgrid_editgrid_change").clone(true)).removeAttr("title")
            }
        });
        gridObj.getRow(0).data("record", null).data("new", "true")
    };
    $.fn.bsgrid.extendOtherMethods.fixedHeader = function(iFirst, options) {
        if ($.trim($("#" + options.gridId + "_fixedDiv").data("fixedGridLock")) == "lock") {
            return
        }
        $("#" + options.gridId + "_fixedDiv").data("fixedGridLock", "lock");
        var headTrNum = $("#" + options.gridId + " thead tr").length;
        if (!iFirst) {
            headTrNum = headTrNum / 2;
            $("#" + options.gridId + " thead tr:lt(" + headTrNum + ")").remove()
        }
        var fixedGridHeight = getSize(options.settings.extend.settings.fixedGridHeight);
        if (fixedGridHeight < $("#" + options.gridId).height()) {
            $("#" + options.gridId + "_fixedDiv").height(fixedGridHeight);
            $("#" + options.gridId).width($("#" + options.gridId + "_fixedDiv").width() - 18);
            $("#" + options.gridId + "_fixedDiv").animate({
                scrollTop: "0px"
            },
            0)
        } else {
            $("#" + options.gridId + "_fixedDiv").height($("#" + options.gridId).height());
            $("#" + options.gridId).width($("#" + options.gridId + "_fixedDiv").width() - 1)
        }
        $("#" + options.gridId + " thead tr:lt(" + headTrNum + ")").clone(true).prependTo("#" + options.gridId + " thead");
        $("#" + options.gridId + " thead tr:lt(" + headTrNum + ")").css({
            "z-index": 10,
            position: "fixed"
        }).width($("#" + options.gridId + " thead tr:last").width());
        $("#" + options.gridId + " thead tr:lt(" + headTrNum + ")").each(function(i) {
            var position = $("#" + options.gridId + " thead tr:eq(" + (headTrNum + i) + ")").position();
            $(this).css({
                top: position.top - getSize($(this).find("th").css("border-top-width")),
                left: position.left
            })
        });
        $("#" + options.gridId + " thead tr:gt(" + (headTrNum - 1) + ")").each(function(ri) {
            $(this).find("th").each(function(i) {
                var thObj = $(this);
                $("#" + options.gridId + " thead tr:eq(" + ri + ") th:eq(" + i + ")").height(thObj.height() + ((ri == headTrNum - 1) ? 2 : 1) * getSize(thObj.css("border-top-width"))).width(thObj.width() + getSize(thObj.css("border-left-width")))
            })
        });
        $("#" + options.gridId + "_fixedDiv").data("fixedGridLock", "");
        function getSize(sizeStr) {
            sizeStr = $.trim(sizeStr).toLowerCase().replace("px", "");
            var sizeNum = parseFloat(sizeStr);
            return isNaN(sizeNum) ? 0 : sizeNum
        }
    };
    $.fn.bsgrid.extendOtherMethods.initFixedHeader = function(gridId, options) {
        $("#" + gridId).wrap('<div id="' + gridId + '_fixedDiv"></div>');
        $("#" + gridId + "_fixedDiv").data("fixedGridLock", "");
        $("#" + gridId + "_fixedDiv").css({
            padding: 0,
            "border-width": 0,
            width: "100%",
            "overflow-y": "auto",
            "margin-bottom": "-1px"
        });
        $("#" + gridId).css({
            width: "auto"
        });
        $("#" + gridId + "_pt_outTab").css({
            "border-top-width": "1px"
        });
        $.fn.bsgrid.extendOtherMethods.fixedHeader(true, options);
        $(window).resize(function() {
            $.fn.bsgrid.extendOtherMethods.fixedHeader(false, options)
        })
    }
})(jQuery); (function(a) {
    a.bsgrid_export = {
        defaults: {
            url: "",
            exportFileName: "export",
            colsProperties: {
                width: 100,
                align: "center",
                exportAttr: "w_export",
                indexAttr: "w_index",
                widthAttr: "width",
                alignAttr: "w_align"
            },
            colWidthPercentmultiplier: 14,
            requestParamsName: {
                exportFileName: "exportFileName",
                colNames: "dataNames",
                colIndexs: "dataIndexs",
                colWidths: "dataLengths",
                colAligns: "dataAligns"
            }
        },
        doExport: function(h, o, d) {
            if (o == undefined) {
                o = {}
            }
            var c = {};
            if (d == undefined) {
                d = {}
            }
            a.extend(true, c, a.bsgrid_export.defaults, d);
            var n = "",
            g = "",
            m = "",
            j = "";
            for (var f = 0; f < h.length; f++) {
                if (a.trim(h.eq(f).attr(c.colsProperties.exportAttr)) != "false") {
                    n = n + "," + a.trim(h.eq(f).text());
                    g = g + "," + a.trim(h.eq(f).attr(c.colsProperties.indexAttr));
                    var l = a.trim(h.eq(f).attr(c.colsProperties.widthAttr)).toLocaleLowerCase();
                    var e = c.colsProperties.width;
                    if (isNaN(l)) {
                        if (l.endWith("px")) {
                            e = parseInt(l.replace("px", ""))
                        } else {
                            if (l.endWith("%")) {
                                l = l.replace("%", "");
                                if (!isNaN(l)) {
                                    e = c.colWidthPercentmultiplier * parseInt(l)
                                }
                            }
                        }
                    }
                    m = m + "," + e;
                    var k = a.trim(h.eq(f).attr(c.colsProperties.alignAttr));
                    if (k == "") {
                        k = c.colsProperties.align
                    }
                    j = j + "," + k
                }
            }
            var b;
            if ((typeof o).toLowerCase() == "string" || o instanceof String) {
                b = (o.startWith("&") ? o.substring(1) : o)
            } else {
                b = a.bsgrid.param(o, true)
            }
            document.location.href = c.url + (c.url.indexOf("?") < 0 ? "?": "&") + c.requestParamsName.exportFileName + "=" + encodeURIComponent(encodeURIComponent(c.exportFileName)) + "&" + c.requestParamsName.colNames + "=" + encodeURIComponent(encodeURIComponent(n.substring(1))) + "&" + c.requestParamsName.colIndexs + "=" + g.substring(1) + "&" + c.requestParamsName.colWidths + "=" + m.substring(1) + "&" + c.requestParamsName.colAligns + "=" + j.substring(1) + (b.length == 0 ? "": ("&" + b))
        }
    }
})(jQuery); (function(a) {
    a.fn.bsgrid_form = {
        defaults: {},
        formObjs: {},
        init: function(e, d) {
            var c = {
                settings: a.extend(true, {},
                a.fn.bsgrid_form.defaults, d),
                formId: e,
                jqueryObj: a("#" + e),
                formType: ""
            };
            var b = {
                options: c,
                addAssistShowFormTags: function() {
                    a.fn.bsgrid_form.addAssistShowFormTags(c)
                },
                showForm: function(f) {
                    a.fn.bsgrid_form.showForm(c, f)
                },
                showOrHideRequireSpan: function(f) {
                    a.fn.bsgrid_form.showOrHideRequireSpan(c, f)
                },
                showOrHideAssistForms: function(f) {
                    a.fn.bsgrid_form.showOrHideAssistForms(c, f)
                },
                showOrHideTag: function(f) {
                    a.fn.bsgrid_form.showOrHideTag(c, f)
                }
            };
            a.fn.bsgrid_form.formObjs[e] = b;
            b.addAssistShowFormTags();
            return b
        },
        getFormObj: function(c) {
            var b = a.fn.bsgrid_form.formObjs[c];
            return b ? b: null
        },
        addAssistShowFormTags: function(b) {
            a(".formInput select", b.jqueryObj).each(function() {
                a(this).before('<input type="text" style="display: none;" />');
                var d = a(this).get(0).attributes;
                for (var e = 0; e < d.length; e++) {
                    var c = d[e].name;
                    if (c.toLowerCase().endWith("able") && a(this).attr(c) == "false") {
                        a(this).prev("input").attr(c, "false")
                    }
                }
            });
            a(".formInput textarea", b.jqueryObj).each(function() {
                a(this).before('<div class="assist" style="display: none;"></div>')
            })
        },
        showForm: function(b, c) {
            b.formType = c;
            this.showOrHideRequireSpan(b, c);
            this.showOrHideAssistForms(b, c);
            this.showOrHideTag(b, c);
            if (c.startWith("view")) {
                a(".formInput :input:not(:button,:submit,:reset)", b.jqueryObj).css({
                    "border-width": "0"
                }).attr("readOnly", "readOnly")
            } else {
                if (c.startWith("add")) {
                    a(".formInput :input:not(:button,:submit,:reset)", b.jqueryObj).css({
                        border: "solid 1px #abadb3"
                    }).removeAttr("readOnly")
                } else {
                    if (c.startWith("edit")) {
                        a(".formInput :input:not(:button,:submit,:reset)", b.jqueryObj).css({
                            border: "solid 1px #abadb3"
                        }).removeAttr("readOnly");
                        a(".formInput :input[" + c + "Able=false]", b.jqueryObj).css({
                            "border-width": "0"
                        }).attr("readOnly", "readOnly")
                    }
                }
            }
        },
        showOrHideRequireSpan: function(b, c) {
            if (c.startWith("view")) {
                a(".formLabel span.require", b.jqueryObj).hide()
            } else {
                if (c.startWith("edit")) {
                    a(".formLabel:has(span.require) ~ .formInput:has(:input[" + c + "Able=false])", b.jqueryObj).prev().find("span.require").hide()
                } else {
                    a(".formLabel span.require", b.jqueryObj).show()
                }
            }
        },
        showOrHideAssistForms: function(b, c) {
            a(".formInput select", b.jqueryObj).each(function() {
                var e = (c.startWith("view") || (c.startWith("edit") && a(this).attr(c + "Able") == "false")) ? "block": "none";
                a(this).prev("input").css("display", e).val(a(this).find("option:selected").text());
                var d = e == "block" ? "none": "block";
                a(this).css("display", d)
            });
            a(".formInput textarea", b.jqueryObj).each(function() {
                var e = (c.startWith("view") || (c.startWith("edit") && a(this).attr(c + "Able") == "false")) ? "block": "none";
                a(this).prev("div").css("display", e).html(a(this).val());
                var d = e == "block" ? "none": "block";
                a(this).css("display", d)
            })
        },
        showOrHideTag: function(b, c) {
            a("*", b.jqueryObj).each(function() {
                var d = a.trim(a(this).attr("showType"));
                if (d != "") {
                    if ((c.startWith("view") || c.startWith("add") || c.startWith("edit")) && ("," + d + ",").indexOf("," + c + ",") > -1) {
                        a(this).show()
                    } else {
                        a(this).hide()
                    }
                }
            })
        }
    }
})(jQuery);