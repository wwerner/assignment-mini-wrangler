import net.wolfgangwerner.miniwrangler.model.config.*
wrangler {
    input {
        column("Order Number")
        column("Year")
        column("Month")
        column("Day")
        column("Product Number")
        column("Product Name")
        column("Count")
        column("Extra Col1")
        column("Extra Col2")
        column("Empty Column")
    }

    record {
        field("OrderID").integerFrom("Order Number")
        field("OrderDate").dateFrom("Year", "Month", "Day")
        field("ProductId").stringFrom("Product Number")
        field("ProductName").productNameFrom("Product Name")
        field("Quantity").decimalFrom("Count", "#,##0.0#")
        field("Unit").staticStringFrom("kg")
    }
}
